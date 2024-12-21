# Étape 1 : Build de l'application
FROM openjdk:17-jdk-slim AS build

# Installer Maven et nettoyer les caches
RUN apt-get update && apt-get install -y maven && apt-get clean

# Définir le répertoire de travail dans l'image
WORKDIR /app

# Copier le pom.xml, mvnw et le dossier .mvn
COPY pom.xml ./
COPY .mvn .mvn/

# Résoudre les dépendances
RUN mvn dependency:resolve

# Copier le code source
COPY src ./src

# Construire le package
RUN mvn package -DskipTests

# Étape 2 : Créer l'image finale avec JDK Alpine
FROM openjdk:17-jdk-slim

# Définir le répertoire de travail dans l'image
WORKDIR /app

# Copier le fichier JAR généré depuis l'étape précédente
COPY --from=build /app/target/*.jar app.jar

# Exposer le port
EXPOSE 8080

# Exécuter l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
