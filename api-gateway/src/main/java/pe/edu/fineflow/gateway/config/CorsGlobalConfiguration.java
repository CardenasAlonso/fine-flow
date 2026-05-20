package pe.edu.fineflow.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsGlobalConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CorsGlobalConfiguration.class);

    @Value("${fineflow.cors.enforce-https:false}")
    private boolean enforceHttps;

    @Bean
    public CorsWebFilter corsWebFilter(
            @Value("${fineflow.cors.allowed-origins:http://localhost:4200}") String allowedOrigins) {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        for (String origin : allowedOrigins.split(",")) {
            String trimmed = origin.trim();
            if ("*".equals(trimmed) && Boolean.TRUE.equals(config.getAllowCredentials())) {
                throw new IllegalStateException("Wildcard CORS origin is incompatible with allowCredentials=true. " +
                        "Specify concrete origins in fineflow.cors.allowed-origins property.");
            }
            if (!trimmed.startsWith("https://")
                    && !trimmed.startsWith("http://localhost")
                    && !trimmed.startsWith("http://127.0.0.1")) {
                log.warn("Non-HTTPS CORS origin configured (allowed in dev only): {}", trimmed);
                if (enforceHttps) {
                    throw new IllegalStateException("HTTPS required for CORS origin: " + trimmed);
                }
            }
            config.addAllowedOrigin(trimmed);
        }
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
