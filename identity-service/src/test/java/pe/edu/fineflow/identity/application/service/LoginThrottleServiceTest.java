package pe.edu.fineflow.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import pe.edu.fineflow.common.exception.AuthException;
import pe.edu.fineflow.common.enums.ErrorCode;
import reactor.test.StepVerifier;

class LoginThrottleServiceTest {

    private final LoginThrottleService throttle = new LoginThrottleService();

    private static final String KEY = "school-1:user@demo.edu.pe";

    @Test
    void permitsRequestsBelowMaxAttempts() {
        throttle.recordFailure(KEY);
        throttle.recordFailure(KEY);
        throttle.recordFailure(KEY);

        StepVerifier.create(throttle.check(KEY))
                .verifyComplete();
    }

    @Test
    void blocksAfterFiveFailures() {
        for (int i = 0; i < 5; i++) {
            throttle.recordFailure(KEY);
        }

        StepVerifier.create(throttle.check(KEY))
                .expectErrorMatches(
                        e -> e instanceof AuthException auth
                                && ErrorCode.RATE_LIMITED.getCode().equals(auth.getCode()))
                .verify();
    }

    @Test
    void resetClearsFailures() {
        for (int i = 0; i < 10; i++) {
            throttle.recordFailure(KEY);
        }
        throttle.reset(KEY);

        StepVerifier.create(throttle.check(KEY))
                .verifyComplete();
    }

    @Test
    void windowExpiryResetsAttempts() throws Exception {
        for (int i = 0; i < 10; i++) {
            throttle.recordFailure(KEY);
        }

        Field windowField = LoginThrottleService.class.getDeclaredField("windowStart");
        windowField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ConcurrentMap<String, Instant> windowStart =
                (ConcurrentMap<String, Instant>) windowField.get(throttle);
        windowStart.put(KEY, Instant.now().minus(Duration.ofMinutes(16)));

        Field attemptsField = LoginThrottleService.class.getDeclaredField("attempts");
        attemptsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ConcurrentMap<String, AtomicInteger> attempts =
                (ConcurrentMap<String, AtomicInteger>) attemptsField.get(throttle);
        attempts.put(KEY, new AtomicInteger(10));

        assertDoesNotThrow(
                () ->
                        StepVerifier.create(throttle.check(KEY))
                                .verifyComplete());

        assertThat(attempts).doesNotContainKey(KEY);
        assertThat(windowStart).doesNotContainKey(KEY);
    }

    @Test
    void recordFailureIncrementsCount() {
        throttle.recordFailure(KEY);
        throttle.recordFailure(KEY);
        throttle.recordFailure(KEY);

        StepVerifier.create(throttle.check(KEY)).verifyComplete();
    }
}