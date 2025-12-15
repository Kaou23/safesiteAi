# SafeSite AI - Backend Server

Architecture backend complète pour la plateforme SafeSite AI de sécurité sur chantier.

## 🏗️ Architecture

```
safesiteAi/
├── docker-compose.yml        # Orchestration Docker
├── ml_service/               # Microservice IA (Python FastAPI)
│   ├── main.py
│   ├── requirements.txt
│   └── Dockerfile
├── backend/                  # API Backend (Spring Boot 3)
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/main/java/com/safesite/
│       ├── entity/           # User, Project, Site, Observation
│       ├── repository/       # JPA Repositories
│       ├── dto/              # Request/Response DTOs
│       ├── service/          # AuthService, RiskAnalysisService
│       ├── controller/       # REST APIs
│       ├── security/         # JWT Authentication
│       └── config/           # Security, DataInitializer
└── frontend/                 # Flutter (séparé)
```

## 🚀 Démarrage Rapide

```bash
# Démarrer tous les services
docker-compose up --build

# Ou en arrière-plan
docker-compose up --build -d
```

## 📡 Endpoints API

### Authentification
- `POST /api/auth/login` - Connexion (retourne JWT)
- `GET /api/auth/health` - Health check

### Projets
- `GET /api/projects` - Liste des projets
- `POST /api/projects` - Créer un projet
- `GET /api/projects/{id}/sites` - Sites d'un projet

### Observations
- `GET /api/observations` - Liste des observations
- `POST /api/observations` - Créer une observation (appelle l'IA)
- `GET /api/observations/site/{siteId}` - Observations d'un site

### ML Service
- `POST http://localhost:8000/predict` - Analyse de risque IA

## 👥 Utilisateurs Démo

| Email | Mot de passe | Rôle |
|-------|-------------|------|
| admin@safesite.ai | pass | ADMIN |
| chef@safesite.ai | pass | CHEF |

## 🔧 Configuration

### Ports
- **8080** - Backend Spring Boot
- **8000** - ML Service FastAPI
- **5432** - PostgreSQL

### Variables d'environnement
```
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/safesitedb
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password
APP_ML_SERVICE_URL=http://ml-service:8000
```

## 🧪 Test de l'API

```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@safesite.ai", "password": "pass"}'

# Test ML Service
curl -X POST http://localhost:8000/predict \
  -H "Content-Type: application/json" \
  -d '{"temperature": 35, "epi_compliance": 80, "fatigue": 7}'
```
