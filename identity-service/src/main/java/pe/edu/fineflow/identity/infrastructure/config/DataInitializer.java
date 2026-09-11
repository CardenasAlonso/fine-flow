package pe.edu.fineflow.identity.infrastructure.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.identity.domain.model.User;
import pe.edu.fineflow.identity.domain.port.out.UserRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String hash = passwordEncoder.encode("admin123");

        Flux.just(
                        createUser("U-ADMIN-001", "admin@demo.edu.pe", hash, "ADMIN", "Admin", "Demo", "school-20188-canete"),
                        createUser("U-STU-001", "student@demo.edu.pe", hash, "STUDENT", "Student", "Demo", "school-20188-canete"),
                        createUser("U-TCH-001", "teacher@demo.edu.pe", hash, "TEACHER", "Teacher", "Demo", "school-20188-canete"))
                .flatMap(u -> ensureUser(u, hash))
                .filter(Boolean::booleanValue)
                .count()
                .doOnSuccess(count -> log.info("Data initialized: {} users created/updated", count))
                .doOnError(e -> log.error("Data initialization failed", e))
                .subscribe();
    }

    private User createUser(String id, String email, String hash, String role,
                            String firstName, String lastName, String schoolId) {
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setPasswordHash(hash);
        u.setRole(role);
        u.setFirstName(firstName);
        u.setLastName(lastName);
        u.setSchoolId(schoolId);
        u.setStatus("ACTIVE");
        u.setCreatedAt(Instant.now());
        return u;
    }

    private Mono<Boolean> ensureUser(User user, String validHash) {
        return userRepository.existsByEmailAndSchoolId(user.getEmail(), user.getSchoolId())
                .flatMap(exists -> {
                    if (exists) {
                        return userRepository.findByEmailAndSchoolId(user.getEmail(), user.getSchoolId())
                                .flatMap(existing -> {
                                    try {
                                        passwordEncoder.matches("admin123", existing.getPasswordHash());
                                        return Mono.just(false);
                                    } catch (Exception e) {
                                        existing.setPasswordHash(validHash);
                                        return userRepository.save(existing).then(Mono.just(true));
                                    }
                                });
                    }
                    return userRepository.save(user).then(Mono.just(true));
                });
    }
}
