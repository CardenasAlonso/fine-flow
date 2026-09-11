package pe.edu.fineflow.support.infrastructure.config;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pe.edu.fineflow.common.util.UuidGenerator;
import pe.edu.fineflow.support.domain.model.FeatureFlag;
import pe.edu.fineflow.support.domain.model.SystemConfig;
import pe.edu.fineflow.support.domain.port.out.FeatureFlagRepositoryPort;
import pe.edu.fineflow.support.domain.port.out.SystemConfigRepositoryPort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private static final String DEMO_SCHOOL = "school-20188-canete";

    private final FeatureFlagRepositoryPort featureFlagRepository;
    private final SystemConfigRepositoryPort systemConfigRepository;

    @Override
    public void run(String... args) {
        seedFeatureFlags()
                .then(seedSystemConfigs())
                .doOnSuccess(
                        count ->
                                log.info(
                                        "Support data initialized for {}: {} flags, {} configs",
                                        DEMO_SCHOOL,
                                        featureFlags().size(),
                                        systemConfigs().size()))
                .doOnError(e -> log.error("Support data initialization failed", e))
                .subscribe();
    }

    private Mono<Long> seedFeatureFlags() {
        return Flux.fromIterable(featureFlags())
                .flatMap(this::ensureFlag)
                .filter(Boolean::booleanValue)
                .count();
    }

    private Mono<Long> seedSystemConfigs() {
        return Flux.fromIterable(systemConfigs())
                .flatMap(this::ensureConfig)
                .filter(Boolean::booleanValue)
                .count();
    }

    private Mono<Boolean> ensureFlag(FeatureFlag flag) {
        return featureFlagRepository
                .findBySchoolAndFeature(DEMO_SCHOOL, flag.getFeatureName())
                .hasElement()
                .flatMap(
                        exists ->
                                exists
                                        ? Mono.just(false)
                                        : featureFlagRepository.save(flag).then(Mono.just(true)));
    }

    private Mono<Boolean> ensureConfig(SystemConfig config) {
        return systemConfigRepository
                .findByKey(DEMO_SCHOOL, config.getConfigKey())
                .hasElement()
                .flatMap(
                        exists ->
                                exists
                                        ? Mono.just(false)
                                        : systemConfigRepository.save(config).then(Mono.just(true)));
    }

    private FeatureFlag flag(String featureName, int enabled, String description,
                             String planRequired) {
        FeatureFlag f = new FeatureFlag();
        f.setId(UuidGenerator.generate());
        f.setSchoolId(DEMO_SCHOOL);
        f.setFeatureName(featureName);
        f.setEnabled(enabled);
        f.setDescription(description);
        f.setPlanRequired(planRequired);
        f.setRolloutPct(100);
        f.setCreatedAt(Instant.now());
        return f;
    }

    private SystemConfig config(String configKey, String configValue, String valueType,
                                String description) {
        SystemConfig c = new SystemConfig();
        c.setId(UuidGenerator.generate());
        c.setSchoolId(DEMO_SCHOOL);
        c.setConfigKey(configKey);
        c.setConfigValue(configValue);
        c.setValueType(valueType);
        c.setDescription(description);
        c.setIsSensitive(0);
        c.setCreatedAt(Instant.now());
        c.setUpdatedAt(Instant.now());
        return c;
    }

    private java.util.List<FeatureFlag> featureFlags() {
        return java.util.List.of(
                flag("AI_CHAT", 1, "Asistente IA con RAG sobre documentos MINEDU", "PREMIUM"),
                flag("BLOCKCHAIN", 1, "Registro inmutable de eventos académicos", "PREMIUM"),
                flag("QR_ATTENDANCE", 1, "Control de entrada por código QR del carnet", "STANDARD"),
                flag("BULK_IMPORT", 1, "Importación masiva de alumnos desde Excel", "STANDARD"),
                flag("ADVANCED_REPORTS", 1, "Reportes avanzados con gráficas (PDF/Excel)", "PREMIUM"),
                flag("GUARDIAN_PORTAL", 1, "Portal web y app para apoderados", "BASIC"),
                flag("MULTI_SECTION_TEACHER", 0,
                        "Docente asignado a múltiples secciones simultáneas", "ENTERPRISE"),
                flag("SSO_GOOGLE", 0, "Login con Google (SSO OAuth2)", "ENTERPRISE"));
    }

    private java.util.List<SystemConfig> systemConfigs() {
        return java.util.List.of(
                config("ATTENDANCE_ABSENCE_ALERT_THRESHOLD", "3", "NUMBER",
                        "Número de faltas consecutivas para generar alerta de asistencia"),
                config("JUSTIFICATION_DEADLINE_HOURS", "48", "NUMBER",
                        "Horas máximas para presentar justificación de inasistencia"),
                config("QR_ROTATION_DAYS", "7", "NUMBER",
                        "Días entre rotaciones del secreto HMAC del QR del carnet"),
                config("LATE_THRESHOLD_MINUTES", "10", "NUMBER",
                        "Minutos de tolerancia antes de marcar tardanza"),
                config("MIN_PASSING_SCORE", "11", "NUMBER",
                        "Nota mínima para aprobar (escala MINEDU 0-20)"),
                config("REPORT_EXPIRY_HOURS", "72", "NUMBER",
                        "Horas de validez de los reportes generados"));
    }
}