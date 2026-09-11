package pe.edu.fineflow.innovation.infrastructure.adapter.out.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.innovation.domain.model.ChatMessage;
import pe.edu.fineflow.innovation.domain.model.ChatSession;
import pe.edu.fineflow.innovation.domain.port.out.ChatSessionRepositoryPort;
import pe.edu.fineflow.innovation.infrastructure.adapter.out.persistence.entity.ChatMessageEntity;
import pe.edu.fineflow.innovation.infrastructure.adapter.out.persistence.entity.ChatSessionEntity;
import pe.edu.fineflow.innovation.infrastructure.adapter.out.persistence.repository.ChatMessageR2dbcRepository;
import pe.edu.fineflow.innovation.infrastructure.adapter.out.persistence.repository.ChatSessionR2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ChatSessionRepositoryAdapter implements ChatSessionRepositoryPort {

    private final ChatSessionR2dbcRepository sessionRepository;
    private final ChatMessageR2dbcRepository messageRepository;

    @Override
    public Mono<ChatSession> save(ChatSession session) {
        return sessionRepository.save(toSessionEntity(session)).map(this::toSessionModel);
    }

    @Override
    public Mono<ChatSession> findByIdAndSchoolId(String id, String schoolId) {
        return sessionRepository.findByIdAndSchoolId(id, schoolId).map(this::toSessionModel);
    }

    @Override
    public Mono<ChatMessage> saveMessage(ChatMessage message) {
        return messageRepository.save(toMessageEntity(message)).map(this::toMessageModel);
    }

    @Override
    public Flux<ChatMessage> findMessagesBySessionId(String sessionId) {
        return messageRepository
                .findBySessionIdOrderByCreatedAtAsc(sessionId)
                .map(this::toMessageModel);
    }

    @Override
    public Mono<Void> closeSession(String sessionId, String schoolId) {
        return sessionRepository
                .findByIdAndSchoolId(sessionId, schoolId)
                .flatMap(
                        s -> {
                            s.setIsActive(0);
                            s.setEndedAt(java.time.Instant.now());
                            return sessionRepository.save(s);
                        })
                .then();
    }

    private ChatSessionEntity toSessionEntity(ChatSession m) {
        ChatSessionEntity e = new ChatSessionEntity();
        e.setId(m.getId());
        e.setSchoolId(m.getSchoolId());
        e.setUserId(m.getUserId());
        e.setUserRole(m.getUserRole());
        e.setSessionToken(m.getSessionToken());
        e.setStartedAt(m.getStartedAt());
        e.setLastMessageAt(m.getLastMessageAt());
        e.setEndedAt(m.getEndedAt());
        e.setIsActive(m.getIsActive() != null && m.getIsActive() == 1 ? 1 : 0);
        return e;
    }

    private ChatSession toSessionModel(ChatSessionEntity e) {
        ChatSession m = new ChatSession();
        m.setId(e.getId());
        m.setSchoolId(e.getSchoolId());
        m.setUserId(e.getUserId());
        m.setUserRole(e.getUserRole());
        m.setSessionToken(e.getSessionToken());
        m.setStartedAt(e.getStartedAt());
        m.setLastMessageAt(e.getLastMessageAt());
        m.setEndedAt(e.getEndedAt());
        m.setIsActive(e.getIsActive());
        return m;
    }

    private ChatMessageEntity toMessageEntity(ChatMessage m) {
        ChatMessageEntity e = new ChatMessageEntity();
        e.setId(m.getId());
        e.setSchoolId(m.getSchoolId());
        e.setSessionId(m.getSessionId());
        e.setRole(m.getRole());
        e.setContent(m.getContent());
        e.setSourcesJson(m.getSourcesJson());
        e.setConfidence(m.getConfidence());
        e.setCreatedAt(m.getCreatedAt());
        return e;
    }

    private ChatMessage toMessageModel(ChatMessageEntity e) {
        ChatMessage m = new ChatMessage();
        m.setId(e.getId());
        m.setSchoolId(e.getSchoolId());
        m.setSessionId(e.getSessionId());
        m.setRole(e.getRole());
        m.setContent(e.getContent());
        m.setSourcesJson(e.getSourcesJson());
        m.setConfidence(e.getConfidence());
        m.setCreatedAt(e.getCreatedAt());
        return m;
    }
}
