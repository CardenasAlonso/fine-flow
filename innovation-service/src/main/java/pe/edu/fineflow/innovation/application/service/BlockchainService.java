package pe.edu.fineflow.innovation.application.service;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.event.AttendanceRecordedEvent;
import pe.edu.fineflow.common.event.EventBus;
import pe.edu.fineflow.common.event.ScoreRegisteredEvent;
import pe.edu.fineflow.common.event.StudentEnrolledEvent;
import pe.edu.fineflow.common.util.UuidGenerator;
import pe.edu.fineflow.innovation.application.port.in.BlockchainUseCase;
import pe.edu.fineflow.innovation.domain.model.BlockchainBlock;
import pe.edu.fineflow.innovation.domain.port.out.BlockchainRepositoryPort;
import java.time.Duration;
import org.springframework.dao.DataIntegrityViolationException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Service
public class BlockchainService implements BlockchainUseCase {

    private final BlockchainRepositoryPort repo;
    private final EventBus eventBus;

    public BlockchainService(BlockchainRepositoryPort repo, EventBus eventBus) {
        this.repo = repo;
        this.eventBus = eventBus;
    }

    /** Escucha eventos de otros servicios y los persiste en la cadena. */
    @PostConstruct
    public void subscribeToEvents() {
        eventBus.stream(AttendanceRecordedEvent.class)
                .flatMap(
                        e -> appendBlock(
                                e.getSchoolId(),
                                e.getTriggeredBy(),
                                "ATTENDANCE",
                                e.getAttendanceId(),
                                "ATTENDANCE",
                                "{\"studentId\":\""
                                        + e.getStudentId()
                                        + "\",\"status\":\""
                                        + e.getStatus()
                                        + "\"}")
                                .doOnError(
                                        err -> log.error(
                                                "Failed to append attendance block", err)))
                .retryWhen(reactor.util.retry.Retry.backoff(3, java.time.Duration.ofMillis(100)))
                .subscribe(
                        block -> {
                        },
                        error -> log.error("Stream error in attendance blockchain events", error));

        eventBus.stream(ScoreRegisteredEvent.class)
                .flatMap(
                        e -> appendBlock(
                                e.getSchoolId(),
                                e.getTriggeredBy(),
                                "SCORE",
                                e.getScoreId(),
                                "STUDENT_SCORE",
                                "{\"studentId\":\""
                                        + e.getStudentId()
                                        + "\",\"score\":"
                                        + e.getScore()
                                        + "}")
                                .doOnError(
                                        err -> log.error(
                                                "Failed to append score block", err)))
                .retryWhen(reactor.util.retry.Retry.backoff(3, java.time.Duration.ofMillis(100)))
                .subscribe(
                        block -> {
                        }, error -> log.error("Stream error in score blockchain events", error));

        eventBus.stream(StudentEnrolledEvent.class)
                .flatMap(
                        e -> appendBlock(
                                e.getSchoolId(),
                                e.getTriggeredBy(),
                                "ENROLLMENT",
                                e.getStudentId(),
                                "STUDENT",
                                "{\"studentId\":\""
                                        + e.getStudentId()
                                        + "\",\"sectionId\":\""
                                        + e.getSectionId()
                                        + "\"}")
                                .doOnError(
                                        err -> log.error(
                                                "Failed to append enrollment block", err)))
                .retryWhen(reactor.util.retry.Retry.backoff(3, java.time.Duration.ofMillis(100)))
                .subscribe(
                        block -> {
                        },
                        error -> log.error("Stream error in enrollment blockchain events", error));
    }

    @Override
    public Mono<BlockchainBlock> appendBlock(
            String schoolId,
            String triggeredBy,
            String eventType,
            String entityId,
            String entityType,
            String payloadJson) {
        return repo.findLatestBySchoolId(schoolId)
                .defaultIfEmpty(genesisPlaceholder(schoolId))
                .flatMap(
                        prev -> {
                            int nextIndex = prev.getBlockIndex() + 1;
                            String previousHash = prev.getHash();

                            BlockchainBlock block = new BlockchainBlock();
                            block.setId(UuidGenerator.generate());
                            block.setSchoolId(schoolId);
                            block.setBlockIndex(nextIndex);
                            block.setEventType(eventType);
                            block.setEntityId(entityId);
                            block.setEntityType(entityType);
                            block.setPayload(payloadJson);
                            block.setPreviousHash(previousHash);
                            block.setHash(computeHash(block, previousHash));
                            block.setCreatedBy(triggeredBy);
                            block.setCreatedAt(Instant.now());
                            return repo.save(block);
                        })
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
                        .filter(t -> t instanceof DataIntegrityViolationException));
    }

    @Override
    public Mono<Boolean> verifyChain(String schoolId) {
        return repo.findAllBySchoolId(schoolId)
                .reduce(new VerifyState(true, null), (state, block) -> {
                    if (!state.valid)
                        return state;
                    if (state.prev == null)
                        return new VerifyState(true, block);
                    if (!block.getPreviousHash().equals(state.prev.getHash())) {
                        log.warn("Previous hash mismatch at block index {}", block.getBlockIndex());
                        return new VerifyState(false, block);
                    }
                    String expectedHash = computeHash(block, block.getPreviousHash());
                    if (!block.getHash().equals(expectedHash)) {
                        log.warn("Hash mismatch at block index {}", block.getBlockIndex());
                        return new VerifyState(false, block);
                    }
                    return new VerifyState(true, block);
                })
                .map(state -> state.valid);
    }

    @Override
    public Flux<BlockchainBlock> getChain(String schoolId) {
        return repo.findAllBySchoolId(schoolId);
    }

    private String computeHash(BlockchainBlock block, String previousHash) {
        String entityId = block.getEntityId() != null ? block.getEntityId() : "";
        String entityType = block.getEntityType() != null ? block.getEntityType() : "";
        String payload = block.getPayload() != null ? block.getPayload() : "";
        String data = block.getBlockIndex()
                + block.getSchoolId()
                + block.getEventType()
                + entityId
                + entityType
                + payload
                + previousHash;
        return sha256(data);
    }

    private BlockchainBlock genesisPlaceholder(String schoolId) {
        BlockchainBlock g = new BlockchainBlock();
        g.setBlockIndex(-1);
        g.setHash("0".repeat(64));
        g.setSchoolId(schoolId);
        return g;
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes)
                sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 failed", e);
        }
    }

    private record VerifyState(boolean valid, BlockchainBlock prev) {
    }
}
