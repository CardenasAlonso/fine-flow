package pe.edu.fineflow.common.config;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryMetadata;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.edu.fineflow.common.tenant.TenantContext;
import reactor.core.publisher.Mono;

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
            Optional<String> schoolIdOpt = TenantContext.getSchoolIdOptional().block();
            if (schoolIdOpt != null && schoolIdOpt.isPresent()) {
                String schoolId = schoolIdOpt.get();
                log.debug("Creating tenant-aware connection for school={}", schoolId);
                return createWithTenant(schoolId);
            } else {
                log.warn("No school context - VPD may not filter data");
                return delegate.create();
            }
        }

        private Publisher<? extends Connection> createWithTenant(String schoolId) {
            return subscriber -> {
                delegate.create().subscribe(new TenantConnectionSubscriber(subscriber, schoolId));
            };
        }

        private class TenantConnectionSubscriber
                implements org.reactivestreams.Subscriber<Connection> {
            private final org.reactivestreams.Subscriber<? super Connection> delegate;
            private final String schoolId;

            public TenantConnectionSubscriber(
                    org.reactivestreams.Subscriber<? super Connection> delegate, String schoolId) {
                this.delegate = delegate;
                this.schoolId = schoolId;
            }

            @Override
            public void onSubscribe(org.reactivestreams.Subscription subscription) {
                delegate.onSubscribe(subscription);
            }

            @Override
            public void onNext(Connection conn) {
                setTenantContext(conn, schoolId)
                        .doOnTerminate(() -> delegate.onNext(conn))
                        .subscribe();
            }

            @Override
            public void onError(Throwable t) {
                log.error("VPD init failed for school {}: {}", schoolId, t.getMessage());
                delegate.onError(t);
            }

            @Override
            public void onComplete() {
                delegate.onComplete();
            }
        }

        private Mono<Void> setTenantContext(Connection conn, String schoolId) {
            return Mono.from(
                            conn.createStatement("BEGIN SP_SET_TENANT_CONTEXT(:schoolId); END;")
                                    .bind("schoolId", schoolId)
                                    .execute())
                    .flatMap(r -> Mono.from(r.getRowsUpdated()))
                    .then()
                    .onErrorResume(
                            e -> {
                                log.warn(
                                        "SP_SET_TENANT_CONTEXT failed (may not exist in dev): {}",
                                        e.getMessage());
                                return Mono.empty();
                            });
        }

        @Override
        public ConnectionFactoryMetadata getMetadata() {
            return delegate.getMetadata();
        }
    }
}
