package pe.edu.fineflow.support.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import pe.edu.fineflow.common.security.JwtProvider;
import pe.edu.fineflow.common.security.ReactiveSecurityConfig;
import pe.edu.fineflow.common.tenant.TenantWebFilter;

@Configuration
public class SecurityConfig extends ReactiveSecurityConfig {

    private final TenantWebFilter tenantWebFilter;

    public SecurityConfig(JwtProvider jwtProvider, TenantWebFilter tenantWebFilter) {
        super(jwtProvider);
        this.tenantWebFilter = tenantWebFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return buildChain(
                http,
                tenantWebFilter,
                "/actuator/**", "/v3/api-docs/**", "/swagger-ui/**", "/webjars/**");
    }
}