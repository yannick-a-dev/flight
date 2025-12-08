FROM maven:3.9.3-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml ./
COPY .mvn .mvn/
RUN mkdir -p /root/.m2
RUN mvn -B dependency:resolve -Dmaven.wagon.http.retryHandler.count=5 -Dmaven.wagon.httpconnectionManager.ttlSeconds=30 || true

COPY src ./src
RUN mvn -B package -DskipTests

FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Installer netcat et curl (attente & healthcheck)
RUN apt-get update && \
    apt-get install -y netcat curl && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/project-flight-0.0.1-SNAPSHOT.jar /app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
