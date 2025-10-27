# ✈️ Flight Management Service

![Spring Boot](https://img.shields.io/badge/SpringBoot-3.x-brightgreen)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)
![Kafka](https://img.shields.io/badge/Kafka-7.8.0-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

---

## 📘 About The Project
**Flight Management Service** est une application Spring Boot pour la gestion des vols, intégrée avec :

- **Apache Kafka** & **Zookeeper** pour la gestion des événements
- **Schema Registry** pour Avro
- **MySQL** pour la persistance des données
- **Mailhog** pour simuler l'envoi d'emails
- **Swagger UI** pour la documentation API

Fonctionnalités principales :

- Gestion des vols et des réservations
- Notifications par e-mail
- Gestion des événements Kafka via Avro
- Documentation API avec Swagger

---

## 🏗️ Built With

- 🧩 Spring Boot
- 🐳 Docker & Docker Compose
- 🐘 MySQL 8
- ⚡ Apache Kafka & Zookeeper
- 📡 Schema Registry
- 📨 Mailhog
- 🛠️ Swagger / Springdoc

---

## 🚀 Getting Started

### Prérequis

Avant de démarrer, installez :

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

### Kafka Topics

event-placed : topic principal pour les événements de vols

Les topics sont auto-créés avec KAFKA_AUTO_CREATE_TOPICS_ENABLE=true


### Swagger & API Docs
Swagger UI : http://localhost:8083/swagger-ui.html

API Docs (OpenAPI) : http://localhost:8083/v3/api-docs

### Installation & Lancement

```bash
# Cloner le projet
git clone https://github.com/votre-utilisateur/project-flight.git
cd project-flight

# Lancer tous les services (build inclus)
docker-compose up --build

# Afficher les logs du backend
docker-compose logs -f flight-backend

# Arrêter tous les conteneurs
docker-compose down

# Supprimer tous les volumes (attention : supprime les données)
docker-compose down -v

# Recompiler uniquement le backend
docker-compose build flight-backend
