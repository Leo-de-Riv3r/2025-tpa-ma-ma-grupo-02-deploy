# ----------------------------------------------------
# ETAPA 1: BUILD (Compila todos los módulos)
# ----------------------------------------------------
FROM maven:3.8.7-eclipse-temurin-17 AS builder
WORKDIR /app

# Copiar el POM raíz y luego el resto del código
COPY pom.xml .
COPY . /app

# Compila todo el proyecto, generando los JARs en sus carpetas /target
RUN mvn clean package -DskipTests

# ----------------------------------------------------
# ETAPA 2: RUNTIME (Crea la imagen final y ligera)
# ----------------------------------------------------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Argumentos de Construcción que serán pasados por Railway
# Ej: MODULE_NAME=agregador, CONTAINER_PORT=5010
ARG MODULE_NAME
ARG CONTAINER_PORT

# Establece el puerto a exponer (el puerto de Spring Boot)
EXPOSE ${CONTAINER_PORT}

# Copiar el JAR ejecutable del módulo específico de la etapa 'builder'
# El nombre del archivo JAR es: <artifactId>-<version>.jar
COPY --from=builder /app/${MODULE_NAME}/target/${MODULE_NAME}-1.0-SNAPSHOT.jar app.jar

# Define el punto de entrada (el comando de inicio)
ENTRYPOINT ["java", "-jar", "app.jar"]