# Usar una imagen base de Java 17
FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiar el JAR del servicio al contenedor
COPY target/*.jar app.jar

# Exponer el puerto del servicio
EXPOSE 8070

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
