FROM maven:4.0.0-rc-5-eclipse-temurin-25-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:25

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/pom.xml pom.xml

ENV DISPLAY=host.docker.internal:0.0
ENTRYPOINT ["java", "-jar","app.jar"]
