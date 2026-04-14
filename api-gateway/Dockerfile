# Build stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy dependency POMs first for better layer caching
COPY pom.xml ./pom.xml
COPY common-library/pom.xml ./common-library/pom.xml
COPY config-server/pom.xml ./config-server/pom.xml
COPY discovery-server/pom.xml ./discovery-server/pom.xml
COPY api-gateway/pom.xml ./api-gateway/pom.xml
COPY identity-service/pom.xml ./identity-service/pom.xml
COPY academic-service/pom.xml ./academic-service/pom.xml
COPY profile-service/pom.xml ./profile-service/pom.xml
COPY evaluation-service/pom.xml ./evaluation-service/pom.xml
COPY support-service/pom.xml ./support-service/pom.xml
COPY innovation-service/pom.xml ./innovation-service/pom.xml
COPY report-service/pom.xml ./report-service/pom.xml

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -DexcludeTransitive -q || true

# Copy source code
COPY common-library ./common-library
COPY config-server ./config-server
COPY discovery-server ./discovery-server
COPY api-gateway ./api-gateway
COPY identity-service ./identity-service
COPY academic-service ./academic-service
COPY profile-service ./profile-service
COPY evaluation-service ./evaluation-service
COPY support-service ./support-service
COPY innovation-service ./innovation-service
COPY report-service ./report-service

# Build the service
RUN mvn package -DskipTests -pl api-gateway -am -q

# Runtime stage
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# Add non-root user for security
RUN addgroup -g 1001 appgroup && adduser -u 1001 -G appgroup -s /bin/sh -D appuser

COPY --from=builder --chown=appuser:appgroup /app/api-gateway/target/*.jar app.jar
USER appuser
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
