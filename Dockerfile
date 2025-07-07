
FROM openjdk:17

COPY build/libs/Spacio-2023-3.0-release.jar /app/Spacio.jar

# Definir el directorio de trabajo
WORKDIR /app

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "Spacio.jar"]