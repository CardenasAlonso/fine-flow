FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /app
COPY pom.xml .
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
RUN mvn install -DskipTests -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/innovation-service/target/*.jar app.jar
COPY Wallet_SchoolManagment /wallet
ENV TNS_ADMIN=/wallet
EXPOSE 8086

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD wget -q --spider http://localhost:8086/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
