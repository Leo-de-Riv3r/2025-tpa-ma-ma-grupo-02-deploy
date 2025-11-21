FROM maven:3.8.7-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
COPY . /app

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

ARG MODULE_NAME
ARG CONTAINER_PORT

EXPOSE ${CONTAINER_PORT}

COPY --from=builder /app/${MODULE_NAME}/target/${MODULE_NAME}-1.0-SNAPSHOT.jar app.jar

# Define el punto de entrada (el comando de inicio)
ENTRYPOINT ["java", "-jar", "app.jar"]