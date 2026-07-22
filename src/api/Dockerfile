# syntax=docker/dockerfile:1

FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:21-jre

WORKDIR /app

RUN useradd --system --uid 10001 --create-home appuser

COPY --from=build /workspace/target/tenderops-api-0.1.0-SNAPSHOT.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]