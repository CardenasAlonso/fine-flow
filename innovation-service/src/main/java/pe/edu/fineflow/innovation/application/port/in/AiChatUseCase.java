package pe.edu.fineflow.innovation.application.port.in;

import java.util.List;
import pe.edu.fineflow.innovation.domain.model.ChatMessage;
import pe.edu.fineflow.innovation.domain.model.ChatSession;
import reactor.core.publisher.Mono;

public interface AiChatUseCase {
    Mono<ChatSession> startSession();

    Mono<ChatMessage> sendMessage(String sessionId, String userMessage);

    Mono<List<ChatMessage>> getHistory(String sessionId);

    Mono<Void> endSession(String sessionId);
}
