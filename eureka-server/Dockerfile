# ============================================
# DOCKERFILE EUREKA SERVER
# ============================================
# Étape 1 : Image de base avec Java 17
# eclipse-temurin = distribution Java officielle (remplace OpenJDK)
# alpine = version légère de Linux (petite, rapide)
FROM eclipse-temurin:17-jdk-alpine

# Étape 2 : Créer un répertoire de travail dans le conteneur
# Tout ce qu'on copiera ensuite ira dans /app
WORKDIR /app

# Étape 3 : Copier le JAR compilé depuis la machine hôte vers le conteneur
# Le JAR est généré par "mvn clean package"
# Le wildcard *.jar permet de ne pas se soucier du nom exact
COPY target/*.jar app.jar

# Étape 4 : Exposer le port utilisé par Eureka
# C'est une documentation, pas une ouverture réelle du port
EXPOSE 8761

# Étape 5 : Commande de démarrage du conteneur
# "java -jar app.jar" = lance l'application Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]