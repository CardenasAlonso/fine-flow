package pe.edu.fineflow.innovation.application.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.exception.ResourceNotFoundException;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import pe.edu.fineflow.innovation.application.port.in.AiChatUseCase;
import pe.edu.fineflow.innovation.domain.model.ChatMessage;
import pe.edu.fineflow.innovation.domain.model.ChatSession;
import pe.edu.fineflow.innovation.domain.port.out.ChatbotGatewayPort;
import pe.edu.fineflow.innovation.domain.port.out.ChatSessionRepositoryPort;
import reactor.core.publisher.Mono;

@Service
public class AiChatService implements AiChatUseCase {

    private final ChatSessionRepositoryPort repo;
    private final ChatbotGatewayPort chatbotGateway;

    public AiChatService(ChatSessionRepositoryPort repo, ChatbotGatewayPort chatbotGateway) {
        this.repo = repo;
        this.chatbotGateway = chatbotGateway;
    }

    @Override
    public Mono<ChatSession> startSession() {
        return TenantContext.getPrincipal()
                .flatMap(p -> {
                    ChatSession session = new ChatSession();
                    session.setId(UuidGenerator.generate());
                    session.setSchoolId(p.schoolId());
                    session.setUserId(p.userId());
                    session.setUserRole(p.role());
                    session.setIsActive(1);
                    session.setStartedAt(Instant.now());
                    return repo.save(session);
                });
    }

    @Override
    public Mono<ChatMessage> sendMessage(String sessionId, String userMessage) {
        return TenantContext.getSchoolId()
                .flatMap(schoolId -> repo.findByIdAndSchoolId(sessionId, schoolId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("ChatSession", sessionId)))
                        .flatMap(session -> {
                            ChatMessage userMsg = buildMessage(schoolId, sessionId, "user", userMessage, null, null);
                            return repo.saveMessage(userMsg)
                                    .then(chatbotGateway.ask(userMessage, sessionId))
                                    .flatMap(reply -> {
                                        BigDecimal conf = reply.confidence() != null
                                                ? new BigDecimal(reply.confidence().toString())
                                                : null;
                                        ChatMessage assistantMsg = buildMessage(
                                                schoolId, sessionId, "assistant", reply.response(), null, conf);
                                        return repo.saveMessage(assistantMsg);
                                    })
                                    .onErrorResume(e -> {
                                        String fallback = "Lo siento, el servicio de IA no está disponible. "
                                                + "Puedes consultar el Currículo Nacional en minedu.gob.pe";
                                        return repo.saveMessage(buildMessage(
                                                schoolId, sessionId, "assistant", fallback, null, BigDecimal.ZERO));
                                    });
                        }));
    }

    @Override
    public Mono<List<ChatMessage>> getHistory(String sessionId) {
        return TenantContext.getSchoolId()
                .flatMap(schoolId -> repo.findByIdAndSchoolId(sessionId, schoolId)
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("ChatSession", sessionId)))
                        .thenMany(repo.findMessagesBySessionId(sessionId))
                        .collectList());
    }

    @Override
    public Mono<Void> endSession(String sessionId) {
        return TenantContext.getSchoolId()
                .flatMap(schoolId -> repo.closeSession(sessionId, schoolId));
    }

    private ChatMessage buildMessage(
            String schoolId, String sessionId, String role, String content,
            String sourcesJson, BigDecimal confidence) {
        ChatMessage m = new ChatMessage();
        m.setId(UuidGenerator.generate());
        m.setSchoolId(schoolId);
        m.setSessionId(sessionId);
        m.setRole(role);
        m.setContent(content);
        m.setSourcesJson(sourcesJson);
        m.setConfidence(confidence);
        m.setCreatedAt(Instant.now());
        return m;
    }
}