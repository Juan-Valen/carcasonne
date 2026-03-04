FROM maven:3.9.6-eclipse-temurin-25-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package

FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/pom.xml pom.xml

ENV DISPLAY=host.docker.internal:0.0
ENTRYPOINT ["java", "-jar","app.jar"]
