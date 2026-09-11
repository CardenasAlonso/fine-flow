package pe.edu.fineflow.innovation.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pe.edu.fineflow.common.exception.ResourceNotFoundException;
import pe.edu.fineflow.common.security.UserPrincipal;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.innovation.domain.model.ChatMessage;
import pe.edu.fineflow.innovation.domain.model.ChatSession;
import pe.edu.fineflow.innovation.domain.port.out.ChatbotGatewayPort;
import pe.edu.fineflow.innovation.domain.port.out.ChatSessionRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class AiChatUseCaseImplTest {

    private ChatSessionRepositoryPort repo;
    private ChatbotGatewayPort chatbotGateway;
    private AiChatUseCaseImpl service;

    @BeforeEach
    void setUp() {
        repo = org.mockito.Mockito.mock(ChatSessionRepositoryPort.class);
        chatbotGateway = org.mockito.Mockito.mock(ChatbotGatewayPort.class);
        service = new AiChatUseCaseImpl(repo, chatbotGateway);
    }

    private ChatSession session() {
        ChatSession s = new ChatSession();
        s.setId("session-1");
        s.setSchoolId("school-1");
        s.setUserId("user-1");
        s.setUserRole("STUDENT");
        s.setIsActive(1);
        return s;
    }

    private ChatMessage message(String role, String content) {
        ChatMessage m = new ChatMessage();
        m.setId("msg-" + role);
        m.setSchoolId("school-1");
        m.setSessionId("session-1");
        m.setRole(role);
        m.setContent(content);
        return m;
    }

    @Test
    void startSessionCreatesSessionForPrincipal() {
        when(repo.save(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        UserPrincipal principal = new UserPrincipal("user-1", "school-1", "a@b.com", "STUDENT", null);

        StepVerifier.create(service.startSession()
                        .contextWrite(ctx -> ctx.put(TenantContext.PRINCIPAL_KEY, principal)))
                .assertNext(session -> {
                    org.assertj.core.api.Assertions.assertThat(session.getSchoolId()).isEqualTo("school-1");
                    org.assertj.core.api.Assertions.assertThat(session.getUserId()).isEqualTo("user-1");
                    org.assertj.core.api.Assertions.assertThat(session.getIsActive()).isEqualTo(1);
                })
                .verifyComplete();
    }

    @Test
    void sendMessageSavesUserMsgAndCallsChatbot() {
        when(repo.findByIdAndSchoolId("session-1", "school-1")).thenReturn(Mono.just(session()));
        when(repo.saveMessage(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(chatbotGateway.ask(eq("¿qué es el currículo?"), eq("session-1")))
                .thenReturn(Mono.just(new ChatbotGatewayPort.ChatbotReply("respuesta IA", new BigDecimal("0.9"))));

        StepVerifier.create(service.sendMessage("session-1", "¿qué es el currículo?")
                        .contextWrite(ctx -> ctx.put(TenantContext.SCHOOL_ID_KEY, "school-1")))
                .assertNext(msg -> org.assertj.core.api.Assertions.assertThat(msg.getContent())
                        .isEqualTo("respuesta IA"))
                .verifyComplete();

        verify(chatbotGateway).ask("¿qué es el currículo?", "session-1");
    }

    @Test
    void sendMessageFailsWhenSessionBelongsToAnotherSchool() {
        when(repo.findByIdAndSchoolId("session-1", "school-1")).thenReturn(Mono.empty());

        StepVerifier.create(service.sendMessage("session-1", "hola")
                        .contextWrite(ctx -> ctx.put(TenantContext.SCHOOL_ID_KEY, "school-1")))
                .expectError(ResourceNotFoundException.class)
                .verify();

        verify(repo, never()).saveMessage(any());
        verify(chatbotGateway, never()).ask(any(), any());
    }

    @Test
    void sendMessageFallsBackWhenChatbotDown() {
        when(repo.findByIdAndSchoolId("session-1", "school-1")).thenReturn(Mono.just(session()));
        when(repo.saveMessage(any())).thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        when(chatbotGateway.ask(any(), any())).thenReturn(Mono.error(new RuntimeException("down")));

        StepVerifier.create(service.sendMessage("session-1", "hola")
                        .contextWrite(ctx -> ctx.put(TenantContext.SCHOOL_ID_KEY, "school-1")))
                .assertNext(msg -> org.assertj.core.api.Assertions.assertThat(msg.getContent())
                        .contains("no está disponible"))
                .verifyComplete();
    }

    @Test
    void getHistoryReturnsOnlyOwnedSessionMessages() {
        when(repo.findByIdAndSchoolId("session-1", "school-1")).thenReturn(Mono.just(session()));
        when(repo.findMessagesBySessionId("session-1"))
                .thenReturn(Flux.just(message("user", "hola"), message("assistant", "hola!")));

        StepVerifier.create(service.getHistory("session-1")
                        .contextWrite(ctx -> ctx.put(TenantContext.SCHOOL_ID_KEY, "school-1")))
                .assertNext(messages -> org.assertj.core.api.Assertions.assertThat(messages).hasSize(2))
                .verifyComplete();
    }

    @Test
    void getHistoryRejectsForeignSession() {
        when(repo.findByIdAndSchoolId("session-1", "school-1")).thenReturn(Mono.empty());

        StepVerifier.create(service.getHistory("session-1")
                        .contextWrite(ctx -> ctx.put(TenantContext.SCHOOL_ID_KEY, "school-1")))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void endSessionClosesOwnedSession() {
        when(repo.closeSession("session-1", "school-1")).thenReturn(Mono.empty());

        StepVerifier.create(service.endSession("session-1")
                        .contextWrite(ctx -> ctx.put(TenantContext.SCHOOL_ID_KEY, "school-1")))
                .verifyComplete();

        verify(repo).closeSession("session-1", "school-1");
    }
}