# 🏥 Clinic Microservices — Gestion de Clinique Médicale

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-green?style=for-the-badge&logo=springboot)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-2023.0-green?style=for-the-badge&logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?style=for-the-badge&logo=docker)
![Kubernetes](https://img.shields.io/badge/Kubernetes-ready-326CE5?style=for-the-badge&logo=kubernetes)
![Claude AI](https://img.shields.io/badge/Claude_AI-intégré-blueviolet?style=for-the-badge)

**Projet académique — Architecture Microservices**

*Application de gestion de clinique médicale construite avec une architecture microservices moderne,
incluant la découverte de services, un API Gateway et une intégration d'intelligence artificielle.*

</div>

---

## 📋 Table des matières

- [À propos du projet](#-à-propos-du-projet)
- [Architecture](#-architecture)
- [Services](#-services)
- [Technologies utilisées](#-technologies-utilisées)
- [Prérequis](#-prérequis)
- [Installation et démarrage](#-installation-et-démarrage)
- [Endpoints API](#-endpoints-api)
- [Intégration IA](#-intégration-ia)
- [Structure du projet](#-structure-du-projet)
- [Auteur](#-auteur)

---

## 🎯 À propos du projet

Ce projet est un **devoir académique** réalisé dans le cadre d'un cours sur les architectures distribuées.
Il implémente un système complet de gestion d'une clinique médicale en suivant les principes
des **microservices** : chaque fonctionnalité est isolée dans son propre service indépendant,
avec sa propre base de données.

### Fonctionnalités principales

- 👤 **Gestion des patients** — création, modification, recherche et archivage des dossiers patients
- 🩺 **Gestion des médecins** — profils médecins, spécialités et disponibilités
- 📅 **Gestion des rendez-vous** — prise de RDV, suivi et historique
- 🤖 **IA intégrée** — suggestion automatique de médecin selon les symptômes décrits par le patient

---

## 🏗️ Architecture

```
                        ┌─────────────────┐
                        │   Client Web /  │
                        │     Mobile      │
                        └────────┬────────┘
                                 │ HTTP
                                 ▼
                        ┌─────────────────┐          ┌──────────────────┐
                        │   API Gateway   │          │  Eureka Server   │
                        │   port: 8080    │◄────────►│   port: 8761     │
                        └────────┬────────┘          │  Service Registry│
                                 │                   └──────────────────┘
              ┌──────────────────┼──────────────────┐
              │                  │                  │
              ▼                  ▼                  ▼
   ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
   │ Patient Service  │ │   RDV Service    │ │ Médecin Service  │
   │   port: 8081     │ │   port: 8082     │ │   port: 8083     │
   └────────┬─────────┘ └────────┬─────────┘ └────────┬─────────┘
            │                    │  ──────► Claude AI  │
            ▼                    ▼                     ▼
   ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
   │  DB Patients     │ │  DB Rendez-vous  │ │  DB Médecins     │
   │  PostgreSQL:5432 │ │  PostgreSQL:5433 │ │  PostgreSQL:5434 │
   └──────────────────┘ └──────────────────┘ └──────────────────┘
```

### Principes respectés

| Principe | Implémentation |
|---|---|
| **Isolation** | Chaque service a sa propre base de données PostgreSQL |
| **Découverte** | Netflix Eureka pour l'enregistrement et la localisation des services |
| **Point d'entrée unique** | Spring Cloud Gateway route toutes les requêtes |
| **Communication** | REST (synchrone) via OpenFeign entre services |
| **Conteneurisation** | Docker + Docker Compose pour le déploiement local |
| **Orchestration** | Kubernetes pour le déploiement en production |

---

## 🔧 Services

### 1. Eureka Server (port 8761)
Serveur de **découverte de services**. Chaque microservice s'y enregistre au démarrage.
L'API Gateway l'interroge pour savoir où envoyer les requêtes.
→ Dashboard : `http://localhost:8761`

### 2. API Gateway (port 8080)
**Point d'entrée unique** de l'application. Reçoit toutes les requêtes client et les route
vers le bon microservice via le load balancer. Gère aussi le CORS.

### 3. Patient Service (port 8081)
CRUD complet pour la gestion des dossiers patients.
- Création et mise à jour de dossiers médicaux
- Recherche multicritère (nom, prénom, email)
- Soft delete (archivage plutôt que suppression physique)
- Gestion des antécédents médicaux

### 4. Médecin Service (port 8083)
CRUD complet pour la gestion des médecins de la clinique.
- Profils médecins avec spécialités
- Gestion des disponibilités
- Recherche par spécialité

### 5. RDV Service (port 8082)
Gestion des rendez-vous + **intégration IA**.
- Prise et annulation de rendez-vous
- Vérification de disponibilité des médecins
- Suggestion de médecin par IA selon les symptômes

---

## 🛠️ Technologies utilisées

| Catégorie | Technologie | Version |
|---|---|---|
| Langage | Java | 17 LTS |
| Framework | Spring Boot | 3.2.3 |
| Microservices | Spring Cloud | 2023.0.0 |
| Service Registry | Netflix Eureka | 4.1.0 |
| API Gateway | Spring Cloud Gateway | 4.1.0 |
| Communication inter-services | OpenFeign | 4.1.0 |
| ORM | Spring Data JPA / Hibernate | 6.4 |
| Base de données | PostgreSQL | 15 |
| Build | Maven | 3.8+ |
| Conteneurisation | Docker + Docker Compose | - |
| Orchestration | Kubernetes | - |
| Intelligence Artificielle | Anthropic Claude API | claude-sonnet-4 |
| Documentation API | (Postman Collection fournie) | - |

---

## ✅ Prérequis

Assure-toi d'avoir installé :

- **Java 17+** → [Télécharger](https://adoptium.net/)
- **Maven 3.8+** → [Télécharger](https://maven.apache.org/download.cgi)
- **PostgreSQL 12+** → [Télécharger](https://www.postgresql.org/download/)
- **Docker Desktop** → [Télécharger](https://www.docker.com/products/docker-desktop/) *(optionnel)*
- **Postman** → [Télécharger](https://www.postman.com/downloads/) *(pour tester l'API)*

---

## 🚀 Installation et démarrage

### Option A — Sans Docker (développement local)

**1. Cloner le projet**
```bash
git clone https://github.com/TON_USERNAME/clinic-microservices.git
cd clinic-microservices
```

**2. Initialiser la base de données**

Exécute le fichier `init-db.sql` dans pgAdmin ou psql :
```bash
psql -U postgres -f init-db.sql
```

**3. Démarrer les services dans cet ordre**

```bash
# Terminal 1 — Eureka Server (démarrer EN PREMIER)
cd eureka-server && mvn spring-boot:run

# Terminal 2 — Patient Service
cd patient-service && mvn spring-boot:run

# Terminal 3 — Médecin Service
cd medecin-service && mvn spring-boot:run

# Terminal 4 — RDV Service
cd rdv-service && mvn spring-boot:run

# Terminal 5 — API Gateway (démarrer EN DERNIER)
cd api-gateway && mvn spring-boot:run
```

**4. Vérifier que tout fonctionne**

- Eureka Dashboard : http://localhost:8761 (tous les services doivent apparaître)
- API Gateway : http://localhost:8080/api/patients
- Patient Service direct : http://localhost:8081/api/patients/health

### Option B — Avec Docker Compose

```bash
git clone https://github.com/TON_USERNAME/clinic-microservices.git
cd clinic-microservices

# Build de tous les services
mvn clean package -DskipTests

# Démarrage complet
docker-compose up -d

# Voir les logs
docker-compose logs -f
```

---

## 📡 Endpoints API

> Toutes les requêtes passent par la Gateway sur le port **8080**.
> La collection Postman complète est disponible dans `postman-collection.json`.

### 👤 Patients — `/api/patients`

| Méthode | Endpoint | Description | Status |
|---|---|---|---|
| `GET` | `/api/patients` | Lister tous les patients actifs | 200 |
| `GET` | `/api/patients?search=dupont` | Rechercher par mot-clé | 200 |
| `GET` | `/api/patients/{id}` | Récupérer un patient | 200 / 404 |
| `POST` | `/api/patients` | Créer un patient | 201 |
| `PUT` | `/api/patients/{id}` | Modifier un patient | 200 / 404 |
| `DELETE` | `/api/patients/{id}` | Archiver un patient | 204 |
| `GET` | `/api/patients/statistiques` | Statistiques globales | 200 |

**Exemple de requête POST :**
```json
{
  "nom": "Dupont",
  "prenom": "Jean",
  "dateNaissance": "1985-03-15",
  "email": "jean.dupont@email.com",
  "telephone": "0612345678",
  "groupeSanguin": "A_POSITIF",
  "antecedentsMedicaux": "Hypertension artérielle"
}
```

### 🩺 Médecins — `/api/medecins`

| Méthode | Endpoint | Description |
|---|---|---|
| `GET` | `/api/medecins` | Lister tous les médecins |
| `GET` | `/api/medecins/{id}` | Récupérer un médecin |
| `GET` | `/api/medecins/specialite/{specialite}` | Filtrer par spécialité |
| `POST` | `/api/medecins` | Créer un médecin |
| `PUT` | `/api/medecins/{id}` | Modifier un médecin |
| `DELETE` | `/api/medecins/{id}` | Archiver un médecin |

### 📅 Rendez-vous — `/api/rdv`

| Méthode | Endpoint | Description |
|---|---|---|
| `GET` | `/api/rdv` | Lister tous les RDV |
| `GET` | `/api/rdv/patient/{patientId}` | RDV d'un patient |
| `GET` | `/api/rdv/medecin/{medecinId}` | RDV d'un médecin |
| `POST` | `/api/rdv` | Créer un rendez-vous |
| `PUT` | `/api/rdv/{id}/statut` | Changer le statut d'un RDV |
| `DELETE` | `/api/rdv/{id}` | Annuler un RDV |
| `POST` | `/api/rdv/suggestion-ia` | 🤖 Suggestion de médecin par IA |

---

## 🤖 Intégration IA

Le **RDV Service** intègre l'API **Claude (Anthropic)** pour suggérer automatiquement
le médecin le plus approprié selon les symptômes décrits par le patient.

**Endpoint :** `POST /api/rdv/suggestion-ia`

```json
// Requête
{
  "symptomes": "J'ai des douleurs thoraciques et des difficultés à respirer depuis 3 jours",
  "patientId": 1
}

// Réponse
{
  "specialiteRecommandee": "Cardiologie",
  "explication": "Les symptômes décrits (douleurs thoraciques + dyspnée) suggèrent une consultation en cardiologie en priorité.",
  "medecinsDisponibles": [
    { "id": 3, "nom": "Dr. Kamga", "specialite": "Cardiologie" }
  ],
  "urgence": "HAUTE"
}
```

---

## 📁 Structure du projet

```
clinic-microservices/
│
├── 📄 pom.xml                          # POM parent Maven (multi-module)
├── 📄 docker-compose.yml               # Déploiement Docker complet
├── 📄 docker-compose-dev.yml           # BDD seulement (dev local)
├── 📄 init-db.sql                      # Script d'init des bases de données
├── 📄 postman-collection.json          # Collection de tests Postman
│
├── 📦 eureka-server/                   # Serveur de découverte (port 8761)
│   └── src/main/
│       ├── java/.../EurekaServerApplication.java
│       └── resources/application.yml
│
├── 📦 api-gateway/                     # Gateway de routage (port 8080)
│   └── src/main/
│       ├── java/.../ApiGatewayApplication.java
│       └── resources/application.yml
│
├── 📦 patient-service/                 # Service patients (port 8081)
│   └── src/main/java/.../
│       ├── entity/Patient.java
│       ├── dto/{PatientRequest, PatientResponse}.java
│       ├── repository/PatientRepository.java
│       ├── service/PatientService.java
│       ├── controller/PatientController.java
│       └── exception/{ResourceNotFoundException, GlobalExceptionHandler}.java
│
├── 📦 medecin-service/                 # Service médecins (port 8083)
│   └── src/main/java/...              # Même structure que patient-service
│
├── 📦 rdv-service/                     # Service rendez-vous + IA (port 8082)
│   └── src/main/java/.../
│       ├── ...                        # Même structure + :
│       ├── client/                    # Feign Clients (appels inter-services)
│       └── ai/ClaudeAiService.java    # Intégration Claude API
│
└── 📦 kubernetes/                      # Manifests Kubernetes
    ├── eureka-deployment.yaml
    ├── gateway-deployment.yaml
    ├── patient-deployment.yaml
    ├── medecin-deployment.yaml
    ├── rdv-deployment.yaml
    └── postgres-*.yaml
```

---

## 👨‍💻 Auteur

**[Ton Prénom Nom]**
Étudiant en [Ton filière — ex: Génie Logiciel / Informatique]
[Ton École / Université]

- GitHub : [@ton_username](https://github.com/ton_username)
- Email : ton.email@ecole.com

---

<div align="center">

**⭐ N'hésite pas à mettre une étoile si ce projet t'a été utile !**

*Projet réalisé dans le cadre du cours d'Architecture Logicielle — [Année]*

</div>