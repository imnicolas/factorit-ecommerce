# Etapa 1: Construcción (Usamos una imagen pesada con Maven y el JDK)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
# Copiamos el pom y descargamos dependencias primero (aprovecha la caché de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline
# Copiamos el código fuente y compilamos el .jar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Usamos una imagen súper liviana solo con el JRE)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copiamos SOLO el .jar generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar
# Ejecutamos la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]