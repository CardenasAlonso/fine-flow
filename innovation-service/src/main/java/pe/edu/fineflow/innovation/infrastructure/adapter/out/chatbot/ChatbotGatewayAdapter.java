package pe.edu.fineflow.innovation.infrastructure.adapter.out.chatbot;

import java.math.BigDecimal;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.fineflow.innovation.domain.port.out.ChatbotGatewayPort;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ChatbotGatewayAdapter implements ChatbotGatewayPort {

    private final WebClient chatbotClient;

    public ChatbotGatewayAdapter(
            @Value("${fineflow.chatbot.url:http://chatbot-service:8086}") String chatbotUrl) {
        this.chatbotClient = WebClient.builder().baseUrl(chatbotUrl).build();
    }

    @Override
    public Mono<ChatbotReply> ask(String message, String sessionId) {
        return chatbotClient
                .post()
                .uri("/api/chat/message")
                .bodyValue(Map.of("message", message, "sessionId", sessionId))
                .retrieve()
                .bodyToMono(Map.class)
                .map(
                        response -> {
                            String answer = (String) response.get("response");
                            Object confObj = response.get("confidence");
                            BigDecimal conf =
                                    confObj != null
                                            ? new BigDecimal(confObj.toString())
                                            : null;
                            return new ChatbotReply(answer, conf);
                        });
    }
}