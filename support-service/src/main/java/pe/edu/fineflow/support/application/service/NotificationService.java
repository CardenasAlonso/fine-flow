package pe.edu.fineflow.support.application.service;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pe.edu.fineflow.common.event.AttendanceRecordedEvent;
import pe.edu.fineflow.common.event.EventBus;
import pe.edu.fineflow.common.event.ScoreRegisteredEvent;
import pe.edu.fineflow.common.tenant.TenantContext;
import pe.edu.fineflow.common.util.UuidGenerator;
import pe.edu.fineflow.support.application.port.in.ManageNotificationUseCase;
import pe.edu.fineflow.support.domain.model.Notification;
import pe.edu.fineflow.support.domain.port.out.NotificationRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class NotificationService implements ManageNotificationUseCase {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepositoryPort repo;
    private final EventBus eventBus;

    public NotificationService(NotificationRepositoryPort repo, EventBus eventBus) {
        this.repo = repo;
        this.eventBus = eventBus;
    }

    /**
     * Se suscribe a los eventos del bus al iniciar. En producción: reemplazar
     * por @KafkaListener.
     */
    @PostConstruct
    public void subscribeToEvents() {
        // Cuando se registra una FALTA → notificar al coordinador
        eventBus.stream(AttendanceRecordedEvent.class)
                .filter(e -> "ABSENT".equals(e.getStatus()))
                .flatMap(
                        e -> createForRole(
                                e.getSchoolId(),
                                "COORDINATOR",
                                "ATTENDANCE_ALERT",
                                "Alerta de Inasistencia",
                                "El alumno "
                                        + e.getStudentId()
                                        + " faltó el "
                                        + e.getAttendanceDate())
                                .doOnError(
                                        err -> log.error(
                                                "Failed to create notification for attendance", err)))
                .retryWhen(reactor.util.retry.Retry.backoff(3, java.time.Duration.ofMillis(100)))
                .subscribe(
                        notification -> {
                        },
                        error -> log.error("Stream error in attendance notifications", error));

        // Cuando se registra una nota → notificar al alumno
        eventBus.stream(ScoreRegisteredEvent.class)
                .flatMap(
                        e -> createInternal(
                                e.getSchoolId(),
                                e.getStudentId(),
                                "SCORE_REGISTERED",
                                "Nueva Nota Registrada",
                                "Tu nota para la actividad ha sido registrada: "
                                        + e.getScore()
                                        + "/20",
                                "/grades")
                                .doOnError(
                                        err -> log.error(
                                                "Failed to create notification for score", err)))
                .retryWhen(reactor.util.retry.Retry.backoff(3, java.time.Duration.ofMillis(100)))
                .subscribe(
                        notification -> {
                        }, error -> log.error("Stream error in score notifications", error));
    }

    @Override
    public Mono<Notification> create(Notification n) {
        n.setId(UuidGenerator.generate());
        n.setCreatedAt(Instant.now());
        return repo.save(n);
    }

    @Override
    public Mono<Notification> createForRole(
            String schoolId, String role, String type, String title, String body) {
        Notification n = new Notification();
        n.setId(UuidGenerator.generate());
        n.setSchoolId(schoolId);
        n.setTargetRole(role);
        n.setNotificationType(type);
        n.setTitle(title);
        n.setBody(body);
        n.setIsRead(0);
        n.setCreatedAt(Instant.now());
        return repo.save(n);
    }

    private Mono<Notification> createInternal(
            String schoolId, String userId, String type, String title, String body, String url) {
        Notification n = new Notification();
        n.setId(UuidGenerator.generate());
        n.setSchoolId(schoolId);
        n.setUserId(userId);
        n.setNotificationType(type);
        n.setTitle(title);
        n.setBody(body);
        n.setActionUrl(url);
        n.setIsRead(0);
        n.setCreatedAt(Instant.now());
        return repo.save(n);
    }

    @Override
    public Flux<Notification> findMyNotifications() {
        return TenantContext.getPrincipal()
                .flatMapMany(p -> repo.findUnreadByUserIdAndSchoolId(p.userId(), p.schoolId()));
    }

    @Override
    public Flux<Notification> findAll(Pageable pageable) {
        return TenantContext.getSchoolId().flatMapMany(sid -> repo.findAllBySchoolId(sid, pageable));
    }

    @Override
    public Mono<Long> countUnread() {
        return TenantContext.getPrincipal()
                .flatMap(p -> repo.countUnreadByUserId(p.userId(), p.schoolId()));
    }

    @Override
    public Mono<Notification> markAsRead(String id) {
        return TenantContext.getSchoolId().flatMap(sid -> repo.markAsRead(id, sid));
    }

    @Override
    public Mono<Void> markAllAsRead() {
        return TenantContext.getPrincipal()
                .flatMap(p -> repo.markAllAsReadByUserId(p.userId(), p.schoolId()));
    }
}
