# Etapa de build (opcional si ya buildeas fuera, pero recomendado)
FROM maven:3.9.6-eclipse-temurin-17 AS build
COPY . .
RUN mvn clean package -DskipTests -pl service-discovery -am

# Etapa final
FROM eclipse-temurin:17-jre-alpine
COPY --from=build /service-discovery/target/service-discovery-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]