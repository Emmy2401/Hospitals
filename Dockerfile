# Utiliser l'image OpenJDK
FROM openjdk:23-jdk-slim
# ou FROM openjdk:21-jdk-slim, etc. selon ta version

# Définir le répertoire de travail
WORKDIR /app

# Copier le jar généré par Maven
COPY target/Hospitals-0.0.1-SNAPSHOT.jar /app/app.jar

# Commande par défaut pour exécuter l'application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
