# Multi-stage build for Render and containerized environments
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy Maven wrapper and dependencies
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline -B || true

# Copy source code and build jar
COPY src src
RUN ./mvnw clean package -DskipTests -B

# Minimal runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV PORT=8083
EXPOSE 8083

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8083} -jar app.jar"]

