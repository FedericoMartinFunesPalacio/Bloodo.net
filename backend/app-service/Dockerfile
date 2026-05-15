# Multi-stage Dockerfile para app-service
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8081
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java","-jar","/app/app.jar"]

