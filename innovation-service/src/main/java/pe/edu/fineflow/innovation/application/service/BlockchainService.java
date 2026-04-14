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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
                        e ->
                                appendBlock(
                                        e.getSchoolId(),
                                        e.getTriggeredBy(),
                                        "ATTENDANCE",
                                        e.getAttendanceId(),
                                        "ATTENDANCE",
                                        "{\"studentId\":\""
                                                + e.getStudentId()
                                                + "\",\"status\":\""
                                                + e.getStatus()
                                                + "\"}"))
                .subscribe(
                        block -> {},
                        error ->
                                log.error(
                                        "Blockchain attendance event failed: {}",
                                        error.getMessage()));

        eventBus.stream(ScoreRegisteredEvent.class)
                .flatMap(
                        e ->
                                appendBlock(
                                        e.getSchoolId(),
                                        e.getTriggeredBy(),
                                        "SCORE",
                                        e.getScoreId(),
                                        "STUDENT_SCORE",
                                        "{\"studentId\":\""
                                                + e.getStudentId()
                                                + "\",\"score\":"
                                                + e.getScore()
                                                + "}"))
                .subscribe(
                        block -> {},
                        error ->
                                log.error("Blockchain score event failed: {}", error.getMessage()));

        eventBus.stream(StudentEnrolledEvent.class)
                .flatMap(
                        e ->
                                appendBlock(
                                        e.getSchoolId(),
                                        e.getTriggeredBy(),
                                        "ENROLLMENT",
                                        e.getStudentId(),
                                        "STUDENT",
                                        "{\"studentId\":\""
                                                + e.getStudentId()
                                                + "\",\"sectionId\":\""
                                                + e.getSectionId()
                                                + "\"}"))
                .subscribe(
                        block -> {},
                        error ->
                                log.error(
                                        "Blockchain enrollment event failed: {}",
                                        error.getMessage()));
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
                            String data =
                                    nextIndex
                                            + schoolId
                                            + eventType
                                            + entityId
                                            + (entityType != null ? entityType : "")
                                            + payloadJson
                                            + previousHash;
                            String hash = sha256(data);

                            BlockchainBlock block = new BlockchainBlock();
                            block.setId(UuidGenerator.generate());
                            block.setSchoolId(schoolId);
                            block.setBlockIndex(nextIndex);
                            block.setEventType(eventType);
                            block.setEntityId(entityId);
                            block.setEntityType(entityType);
                            block.setPayload(payloadJson);
                            block.setPreviousHash(previousHash);
                            block.setHash(hash);
                            block.setCreatedBy(triggeredBy);
                            block.setCreatedAt(Instant.now());
                            return repo.save(block);
                        });
    }

    @Override
    public Mono<Boolean> verifyChain(String schoolId) {
        return repo.findAllBySchoolId(schoolId)
                .collectList()
                .map(
                        blocks -> {
                            for (int i = 1; i < blocks.size(); i++) {
                                BlockchainBlock curr = blocks.get(i);
                                BlockchainBlock prev = blocks.get(i - 1);
                                if (!curr.getPreviousHash().equals(prev.getHash())) return false;
                                String expectedHash =
                                        sha256(
                                                curr.getBlockIndex()
                                                        + schoolId
                                                        + curr.getEventType()
                                                        + curr.getEntityId()
                                                        + curr.getEntityType()
                                                        + curr.getPayload()
                                                        + curr.getPreviousHash());
                                if (!curr.getHash().equals(expectedHash)) return false;
                            }
                            return true;
                        });
    }

    @Override
    public Flux<BlockchainBlock> getChain(String schoolId) {
        return repo.findAllBySchoolId(schoolId);
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
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 failed", e);
        }
    }
}
