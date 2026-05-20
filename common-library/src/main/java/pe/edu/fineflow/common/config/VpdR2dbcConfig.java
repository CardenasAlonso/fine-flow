package pe.edu.fineflow.common.config;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pe.edu.fineflow.common.tenant.TenantContext;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class VpdR2dbcConfig {

    @Bean
    @Primary
    public ConnectionFactory tenantAwareConnectionFactory(ConnectionFactory originalFactory) {
        return new TenantAwareConnectionFactory(originalFactory);
    }

    private static class TenantAwareConnectionFactory implements ConnectionFactory {
        private final ConnectionFactory delegate;

        public TenantAwareConnectionFactory(ConnectionFactory delegate) {
            this.delegate = delegate;
        }

        @Override
        public Publisher<? extends Connection> create() {
            return Mono.from(delegate.create())
                    .flatMap(conn -> TenantContext.getSchoolId()
                            .flatMap(schoolId -> Mono.from(conn.createStatement(
                                                    "BEGIN SP_SET_TENANT_CONTEXT(:schoolId); END;")
                                            .bind("schoolId", schoolId)
                                            .execute())
                                    .flatMap(r -> Mono.from(r.getRowsUpdated()))
                                    .then()
                                    .doOnSuccess(v -> log.debug("VPD context set for school={}", schoolId))
                                    .doOnError(e -> log.warn("SP_SET_TENANT_CONTEXT failed: {}", e.getMessage()))
                                    .thenReturn(conn))
                            .switchIfEmpty(Mono.just(conn)));
        }

        @Override
        public ConnectionFactoryMetadata getMetadata() {
            return delegate.getMetadata();
        }
    }
}
