# Multi-stage Dockerfile for api-gateway
# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace
# Copy pom and source
COPY pom.xml ./
COPY src ./src
# Build the jar (skip tests to speed up; in CI you should run them)
RUN mvn -B -DskipTests package

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy jar from build stage (assumes standard spring-boot jar name)
COPY --from=build /workspace/target/*.jar app.jar

# Expose application port
EXPOSE 8080

# Default environment: run with prod profile
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application
ENTRYPOINT ["java","-jar","/app/app.jar"]

