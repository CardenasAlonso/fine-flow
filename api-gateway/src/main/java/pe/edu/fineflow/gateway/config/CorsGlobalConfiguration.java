package pe.edu.fineflow.gateway.config;

import java.util.Arrays;
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

    @Value("${spring.profiles.active:}")
    private String activeProfile;

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
            boolean isDevProfile = Arrays.asList(activeProfile.split(",")).contains("dev");
            if (!trimmed.startsWith("https://")) {
                boolean isLocalhost = trimmed.startsWith("http://localhost") || trimmed.startsWith("http://127.0.0.1");
                if (!isLocalhost || !isDevProfile) {
                    if (enforceHttps) {
                        throw new IllegalStateException("HTTPS required for CORS origin: " + trimmed);
                    }
                    log.warn("Non-HTTPS CORS origin configured (allowed in dev only): {}", trimmed);
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
