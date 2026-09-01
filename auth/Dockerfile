# 1단계: Gradle 환경에서 소스코드 빌드
FROM gradle:8.6-jdk17 AS builder
WORKDIR /build
COPY . .
RUN gradle clean bootJar --no-daemon

# 2단계: 가벼운 JRE 환경에서 실행
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
