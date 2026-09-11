package pe.edu.fineflow.innovation.domain.port.out;

import java.math.BigDecimal;
import reactor.core.publisher.Mono;

public interface ChatbotGatewayPort {

    record ChatbotReply(String response, BigDecimal confidence) {}

    Mono<ChatbotReply> ask(String message, String sessionId);
}