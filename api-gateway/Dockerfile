# ============================================
# DOCKERFILE API GATEWAY
# ============================================
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copier le JAR compilé
COPY target/*.jar app.jar

# Exposer le port de la Gateway
EXPOSE 8080

# Démarrer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]