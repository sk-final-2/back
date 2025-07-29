# Stage 1: Build the JAR using Gradle
FROM gradle:8.2.1-jdk17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew build -x test --no-daemon
# Stage 2: Run the app
FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# 환경 설정: 프로파일을 prod로 고정
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
