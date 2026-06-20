FROM maven:3.9-eclipse-temurin-11 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -Dmaven.test.skip=true package spring-boot:repackage

FROM eclipse-temurin:11-jre

WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=portable
ENV SERVER_PORT=8084

EXPOSE 8084

COPY --from=build /app/target/automata.war /app/automata.war

ENTRYPOINT ["java", "-jar", "/app/automata.war"]
