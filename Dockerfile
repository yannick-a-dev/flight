# ---------- STAGE 1 : BUILD ----------
FROM maven:3.9.3-eclipse-temurin-17 AS build

WORKDIR /app

# Copier tout le projet
COPY . .

# Build du projet (génère le jar)
RUN mvn clean package -DskipTests

# DEBUG (tu peux supprimer après validation)
RUN echo "==== CONTENU DU DOSSIER TARGET ====" && ls -l target


# ---------- STAGE 2 : RUNTIME ----------
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Installer outils utiles (optionnel mais pratique)
RUN apt-get update && \
    apt-get install -y netcat curl && \
    rm -rf /var/lib/apt/lists/*

# Copier le jar depuis le stage build
COPY --from=build /app/target/*.jar /app.jar

# Vérification (optionnelle mais utile au début)
RUN ls -l /app.jar

EXPOSE 8080

# Lancement de l'application
ENTRYPOINT ["sh", "-c", "echo 'Waiting for MySQL...' && until nc -z mysql-flight 3306; do sleep 2; done && echo 'MySQL is up!' && java -jar /app.jar"]