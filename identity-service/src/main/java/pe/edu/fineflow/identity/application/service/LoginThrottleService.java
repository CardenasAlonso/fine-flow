package pe.edu.fineflow.identity.application.service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.exception.AuthException;
import reactor.core.publisher.Mono;

@Service
public class LoginThrottleService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration WINDOW = Duration.ofMinutes(15);

    private final ConcurrentMap<String, AtomicInteger> attempts = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Instant> windowStart = new ConcurrentHashMap<>();

    public Mono<Void> check(String key) {
        AtomicInteger count = attempts.computeIfAbsent(key, k -> new AtomicInteger(0));
        Instant start = windowStart.computeIfAbsent(key, k -> Instant.now());
        if (start.plus(WINDOW).isBefore(Instant.now())) {
            attempts.remove(key);
            windowStart.remove(key);
            return Mono.empty();
        }
        if (count.get() >= MAX_ATTEMPTS) {
            return Mono.error(AuthException.rateLimited());
        }
        return Mono.empty();
    }

    public void recordFailure(String key) {
        attempts.computeIfAbsent(key, k -> new AtomicInteger(0)).incrementAndGet();
    }

    public void reset(String key) {
        attempts.remove(key);
        windowStart.remove(key);
    }
}