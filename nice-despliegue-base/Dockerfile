# Etapa 1: Construcción de la aplicación con Maven
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos de Maven al contenedor
COPY pom.xml .
COPY src ./src

# Construye la aplicación Spring Boot (omitiendo tests para mayor velocidad)
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final de ejecución
FROM eclipse-temurin:21-jdk-alpine

# Crear un usuario y grupo no root para aumentar la seguridad
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Establece el directorio de trabajo
WORKDIR /app

# Copia el archivo jar generado en la etapa de construcción
COPY --from=builder /app/target/*.jar app.jar

# Cambia la propiedad de los archivos a 'appuser'
RUN chown -R appuser:appgroup /app

# Expone el puerto de la aplicación
EXPOSE 8003

# Cambia al usuario no root para la ejecución
USER appuser

# Comando por defecto para ejecutar la aplicación
CMD ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
