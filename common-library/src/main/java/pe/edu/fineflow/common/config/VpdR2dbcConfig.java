package pe.edu.fineflow.common.config;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class VpdR2dbcConfig {

    @Bean
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
            log.debug("Creating connection via tenant-aware factory");
            return delegate.create();
        }

        @Override
        public ConnectionFactoryMetadata getMetadata() {
            return delegate.getMetadata();
        }
    }
}
