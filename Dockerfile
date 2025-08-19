# Étape 1 : Build de l'application avec Maven
FROM maven:3.9.3-eclipse-temurin-17 AS build

WORKDIR /app

# Copier uniquement le pom.xml et .mvn pour tirer parti du cache Docker
COPY pom.xml ./
COPY .mvn .mvn/

# Créer un dossier pour le cache Maven
RUN mkdir -p /root/.m2

# Télécharger les dépendances avec retry et timeout pour fiabilité
RUN mvn -B dependency:resolve -Dmaven.wagon.http.retryHandler.count=5 -Dmaven.wagon.httpconnectionManager.ttlSeconds=30 || true

# Copier le code source
COPY src ./src

# Build du projet sans tests
RUN mvn -B package -DskipTests

# Étape 2 : Image finale
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copier le JAR généré
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
