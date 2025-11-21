# Etapa 1: build con Maven
FROM maven:3.9.0-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: runtime
FROM eclipse-temurin:17
WORKDIR /app
COPY --from=build /app/target/springboot-web-0.0.1-SNAPSHOT.jar app.jar

# Este paso es crucial
EXPOSE 8080

# Este también es crucial para que use el puerto dinámico asignado
ENV PORT=$PORT


# Comando de inicio
CMD ["java", "-jar", "app.jar"]
