# ETAPA 1: Construir el JAR (Usamos Maven)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ETAPA 2: Ejecutar lo que se cocinó
# CORRECCIÓN AQUÍ: Usamos eclipse-temurin en lugar de openjdk
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]