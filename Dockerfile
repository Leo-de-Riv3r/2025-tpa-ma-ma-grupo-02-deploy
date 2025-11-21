FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiamos todo el repo (importante para multimodulo)
COPY . .

# Build SOLO del módulo seleccionado
ARG MODULE_PATH
RUN mvn -q -DskipTests -pl ${MODULE_PATH} -am package

FROM eclipse-temurin:17-jre
WORKDIR /app

ARG MODULE_PATH
ARG MODULE_NAME

COPY --from=builder /app/${MODULE_PATH}/target/${MODULE_NAME}.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
