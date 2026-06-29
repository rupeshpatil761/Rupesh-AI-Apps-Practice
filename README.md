# Rupesh AI Apps Practice — Full-Stack Multi-App Monorepo

A comprehensive full-stack development workspace featuring **two independent applications** with modern architectures, supporting both **local development** and **Docker containerization** with advanced features like pgvector and Ollama.

---

## 📋 Table of Contents

1. [Project Overview](#project-overview)
2. [Applications Summary](#applications-summary)
3. [Complete Architecture](#complete-architecture)
4. [Quick Start – Local Development](#quick-start--local-development)
5. [Quick Start – Docker Deployment](#quick-start--docker-deployment)
6. [Project Structure](#project-structure)
7. [Tech Stack by Application](#tech-stack-by-application)
8. [API Endpoints](#api-endpoints)
9. [Troubleshooting](#troubleshooting)
10. [Advanced Features](#advanced-features)

---

## Project Overview

This monorepo contains **two complete full-stack applications**:

### 🏥 **Clinicals System**
A healthcare data management system for tracking patient information and clinical measurements.

### 🛒 **E-Commerce System**
A modern e-commerce platform with product management, AI-powered features (Anthropic), and vector embeddings (Ollama).

Both applications can run:
- ✅ **Locally** on your development machine
- ✅ **Dockerized** using Docker Compose with PostgreSQL + pgvector + Ollama

---

## Applications Summary

| Feature | Clinicals System | E-Commerce System |
|---------|------------------|-------------------|
| **Frontend** | clinicals-ui-app | Ecom-FrontEnd-AI |
| **Backend** | clinicalsapi | SpringEcomBackend |
| **Frontend Tech** | React 19 + React Router DOM | React 18 + Vite + Bootstrap |
| **Backend Tech** | Spring Boot 4.0.6 | Spring Boot 4.1.0 + Spring AI |
| **Database** | PostgreSQL | PostgreSQL + pgvector |
| **Frontend Port** | 3000 | 3000 (shared) |
| **Backend Port** | 8080 | 8080 (shared) |
| **AI Integration** | ❌ None | ✅ Ollama + Anthropic |
| **Vector DB** | ❌ None | ✅ pgvector |
| **Docker** | Manual | Orchestrated |

---

## Complete Architecture

### **Hybrid Architecture: Local + Docker**

```
┌─────────────────────────────────────────────────────────────────────┐
│                        USER BROWSER                                  │
│                  http://localhost:3000                                │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────────────────┐  OR  ┌──────────────────────────────┐  │
│  │ clinicals-ui-app        │      │ Ecom-FrontEnd-AI (Vite)      │  │
│  │ (React 19)              │      │ (React 18)                   │  │
│  └────────────┬────────────┘      └──────────────┬───────────────┘  │
│               │ HTTP/REST                        │ HTTP/REST         │
└───────────────┼──────────────────────────────────┼──────────────────┘
                │                                  │
                ▼                                  ▼
    ┌───────────────────────────────────────────────────────┐
    │   SPRING BOOT BACKEND  (port 8080)                   │
    │                                                       │
    │   clinicalsapi            SpringEcomBackend          │
    │   • REST Controllers      • REST Controllers         │
    │   • JPA/Hibernate         • Spring AI Integration    │
    │   • Exception Handling    • Ollama Integration       │
    │   • CORS Config           • pgvector Integration     │
    │                           • Anthropic API Integration│
    └──────────────┬────────────────────┬──────────────────┘
                   │                    │
                   ▼                    ▼
    LOCAL DEV              DOCKER CONTAINERS
    ┌──────────────┐       ┌─────────────────────────────┐
    │ PostgreSQL   │       │  Docker Network (ecom_network)
    │ localhost    │       │  ┌──────────┐  ┌──────────┐ │
    │ :5432        │       │  │Postgres  │  │ Ollama   │ │
    │ 'clinicals'  │       │  │pgvector  │  │(LLM+Emb) │ │
    │ or 'ecom-db' │ ←────→│  │:5433     │  │ :11434   │ │
    └──────────────┘       │  └──────────┘  └──────────┘ │
                           │  ┌─────────────────────────┐ │
                           │  │ ollama-init              │ │
                           │  │ (auto-pulls models)     │ │
                           │  └─────────────────────────┘ │
                           └─────────────────────────────┘
```

---

## Quick Start – Local Development

### **For Clinicals System**

#### 1️⃣ Start PostgreSQL

Ensure PostgreSQL is running. Create the database:

```bash
CREATE DATABASE clinicals;
```

#### 2️⃣ Start Backend (clinicalsapi)

```bash
cd clinicalsapi
mvn spring-boot:run
```

Backend: http://localhost:8080

#### 3️⃣ Start Frontend (clinicals-ui-app)

```bash
cd clinicals-ui-app
npm install
npm start
```

Frontend: http://localhost:3000

---

### **For E-Commerce System**

#### 1️⃣ Start PostgreSQL

Ensure PostgreSQL is running. Create the database:

```bash
CREATE DATABASE ecom-db;
```

#### 2️⃣ (Optional) Start Ollama Locally

If you want local AI features without Docker:

```bash
# macOS
brew install ollama
ollama serve

# Other OS: https://ollama.ai/download
```

#### 3️⃣ Start Backend (SpringEcomBackend)

```bash
cd SpringEcomBackend
# Set Anthropic API key (if using AI features)
export SPRING_AI_ANTHROPIC_API_KEY=sk-ant-api03-your_key_here
mvn spring-boot:run
```

Backend: http://localhost:8080

#### 4️⃣ Start Frontend (Ecom-FrontEnd-AI)

```bash
cd Ecom-FrontEnd-AI
npm install
npm run dev
```

Frontend: http://localhost:5173 (Vite dev server) or http://localhost:3000 (if configured)

---

## Quick Start – Docker Deployment

### **Prerequisites**

- ✅ Docker installed: https://docs.docker.com/get-docker/
- ✅ Docker Compose (included with Docker Desktop)
- ✅ Anthropic API Key (for E-Commerce AI features): https://console.anthropic.com/

### **For E-Commerce System (Full Docker Stack)**

#### 1️⃣ Set Environment Variables

```bash
# Create .env file in project root
ANTHROPIC_API_KEY=sk-ant-api03-your_actual_key_here
```

#### 2️⃣ Start All Services

```bash
docker-compose up -d
```

#### 3️⃣ Access Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **PostgreSQL (pgvector)**: localhost:5433 (user: postgres, password: postgres)
- **Ollama API**: http://localhost:11434

#### 4️⃣ Monitor Progress

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
docker-compose logs -f ollama
```

#### 5️⃣ Common Commands

```bash
# Stop all services
docker-compose down

# Stop and remove all data
docker-compose down -v

# Rebuild after code changes
docker-compose up -d --build

# Rebuild specific service
docker-compose up -d --build backend
docker-compose up -d --build frontend

# Access container shell
docker-compose exec backend /bin/sh
docker-compose exec frontend /bin/sh
docker-compose exec postgres psql -U postgres -d ecom-db

# View database
docker-compose exec postgres psql -U postgres -d ecom-db -c "\dt"
```

---

```
Rupesh-AI-Apps-Practice/
│
├── README.md                           ← YOU ARE HERE
├── docker-compose.yml                  (E-Commerce full Docker stack)
├── DOCKER_SETUP.md                     (Detailed Docker guide)
├── docker-start.bat                    (Windows batch script)
├── init_db.sql                         (Database initialization)
│
├── 🏥 CLINICALS SYSTEM
│   ├── clinicalsapi/                   ← Spring Boot Backend
│   │   ├── src/main/java/.../
│   │   │   ├── controllers/            (REST: Patient, Clinical Data)
│   │   │   ├── models/                 (JPA entities)
│   │   │   ├── repositories/           (Spring Data JPA)
│   │   │   ├── exceptions/             (GlobalExceptionHandler)
│   │   │   ├── dto/                    (Request/Response DTO)
│   │   │   └── config/                 (CORS, Logging)
│   │   ├── src/main/resources/
│   │   │   ├── application.properties
│   │   │   └── db/
│   │   ├── pom.xml
│   │   └── target/
│   │
│   └── clinicals-ui-app/               ← React 19 Frontend
│       ├── src/
│       │   ├── App.js                  (Routes, Toast Container)
│       │   ├── components/
│       │   │   ├── Home.js             (Patient list, modals)
│       │   │   ├── AddPatient.js       (Patient form)
│       │   │   └── AddClinicals.js     (Clinical data form)
│       │   ├── App.css
│       │   └── index.js
│       ├── public/
│       ├── package.json                (React 19, Router 7)
│       └── screenshots/
│
├── 🛒 E-COMMERCE SYSTEM
│   ├── SpringEcomBackend/              ← Spring Boot 4.1.0 Backend
│   │   ├── src/main/java/.../
│   │   │   ├── controller/             (REST endpoints)
│   │   │   ├── service/                (Business logic)
│   │   │   ├── repository/             (Spring Data JPA)
│   │   │   ├── model/                  (JPA entities)
│   │   │   └── config/                 (AI, DB, CORS)
│   │   ├── src/main/resources/
│   │   │   └── application.properties
│   │   ├── Dockerfile
│   │   ├── pom.xml                     (Spring AI, Spring Data)
│   │   └── target/
│   │
│   └── Ecom-FrontEnd-AI/               ← React 18 + Vite Frontend
│       ├── src/
│       │   ├── main.jsx
│       │   ├── App.jsx
│       │   ├── components/             (UI components)
│       │   ├── Context/                (State management)
│       │   ├── assets/
│       │   └── index.css
│       ├── public/
│       ├── Dockerfile
│       ├── vite.config.js
│       ├── package.json                (React 18, Vite, Bootstrap)
│       └── README.md
│
└── copilot-java-practice/              ← Standalone Java practice
    ├── src/
    └── pom.xml
```

---

## Tech Stack by Application

### **Clinicals System**

| Layer | Technology | Version |
|-------|-----------|---------|
| **Frontend Framework** | React | 19.2.7 |
| **Frontend Routing** | React Router DOM | 7.17.0 |
| **HTTP Client** | Axios | 1.17.0 |
| **UI Notifications** | React Toastify | 11.1.0 |
| **Testing** | React Testing Library | 16.3.2 |
| **Backend Framework** | Spring Boot | 4.0.6 |
| **Backend Language** | Java | 21 (LTS) |
| **Data Access** | Spring Data JPA + Hibernate | Latest |
| **Database** | PostgreSQL | 16+ |
| **Build Tool** | Maven | 3.9+ |
| **Error Handling** | Global Exception Handler | — |
| **Logging** | SLF4J + Logback | — |

### **E-Commerce System**

| Layer | Technology | Version |
|-------|-----------|---------|
| **Frontend Framework** | React | 18.2.0 |
| **Frontend Build Tool** | Vite | 5.2.8 |
| **CSS Framework** | Bootstrap | 5.3.3 |
| **UI Components** | React Bootstrap | 2.10.2 |
| **Chat UI** | ChatScope Chat UI Kit | 2.0.3 |
| **HTTP Client** | Axios | 1.6.8 |
| **UI Notifications** | React Toastify | 11.0.5 |
| **Testing** | React Testing Library | — |
| **Backend Framework** | Spring Boot | 4.1.0 |
| **Backend Language** | Java | 21 (LTS) |
| **Spring AI** | Spring AI | 2.0.0 |
| **AI Provider** | Anthropic (Claude Haiku) | — |
| **LLM + Embeddings** | Ollama | Latest |
| **Vector Database** | PostgreSQL + pgvector | 16 |
| **Data Access** | Spring Data JPA + Hibernate | Latest |
| **Database** | PostgreSQL | 16+ |
| **Build Tool** | Maven | 3.9+ |
| **Containerization** | Docker + Docker Compose | Latest |

---

## API Endpoints

### **Clinicals System**

| Action | Method | Endpoint | Status |
|--------|--------|----------|--------|
| List all patients | `GET` | `/api/patients` | 200 |
| Add new patient | `POST` | `/api/patients` | 201 |
| Delete patient | `DELETE` | `/api/patients/{id}` | 200 |
| Add clinical data | `POST` | `/api/clinicaldata/clinicals` | 201 |
| View clinical data | `GET` | `/api/clinicaldata/patient/{patientId}` | 200 |

### **E-Commerce System**

| Action | Method | Endpoint | Status |
|--------|--------|----------|--------|
| List all products | `GET` | `/api/products` | 200 |
| Get product by ID | `GET` | `/api/product/{id}` | 200, 404 |
| Get product image | `GET` | `/api/product/{productId}/image` | 200, 404 |
| Create product | `POST` | `/api/product` | 201 |
| Update product | `PUT` | `/api/product/{id}` | 200, 500 |
| Delete product | `DELETE` | `/api/product/{id}` | 200, 404 |
| Search products | `GET` | `/api/product/search?keyword=xyz` | 200 |
| **AI Features** | — | — | — |
| Generate AI description | `POST` | `/api/ai/generate-description` | 200 |
| Find similar products | `POST` | `/api/ai/similarities` | 200 |

---

## Troubleshooting

### **Local Development Issues**

#### Port Already in Use

```bash
# Windows - Find process using port
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# macOS/Linux
lsof -i :8080
kill -9 <PID>
```

#### Database Connection Refused

```bash
# Verify PostgreSQL is running
psql -U postgres -d postgres -c "SELECT 1"

# Create database if missing
psql -U postgres -c "CREATE DATABASE clinicals;"
psql -U postgres -c "CREATE DATABASE ecom-db;"
```

#### Frontend Can't Reach Backend

1. Ensure backend is running on correct port (8080)
2. Check CORS configuration in backend
3. Verify API base URL in frontend `.env` or Axios config

#### Node Modules Issues

```bash
# Clear and reinstall
rm -rf node_modules package-lock.json
npm install
```

---

### **Docker Issues**

#### Backend Won't Start – Connection Refused

```bash
# Restart all services
docker-compose down
docker-compose up -d

# Check logs
docker-compose logs backend
```

#### Port Already in Use in Docker

Edit `docker-compose.yml`:

```yaml
backend:
  ports:
    - "9090:8080"  # Access at 9090 instead of 8080
```

Then restart:

```bash
docker-compose up -d
```

#### Frontend Can't Reach Backend in Docker

The frontend service must use the Docker service name:

```yaml
# In Ecom-FrontEnd-AI Dockerfile or build config
ARG REACT_BASE_URL=http://backend:8080
```

#### Database Connection in Docker

```bash
# Access PostgreSQL inside Docker
docker-compose exec postgres psql -U postgres -d ecom-db

# Check if tables exist
\dt
```

#### Ollama Model Not Found

```bash
# Manually pull models
docker-compose exec ollama ollama pull qwen2.5:0.5b
docker-compose exec ollama ollama pull nomic-embed-text

# List available models
docker-compose exec ollama ollama list
```

### **Rebuild Everything from Scratch**

```bash
# Full cleanup
docker-compose down -v
docker system prune -a

# Fresh start
docker-compose up -d --build

# Verify all services
docker-compose ps
```

---

## Advanced Features

### **pgvector Integration (E-Commerce)**

The E-Commerce system uses PostgreSQL with pgvector extension for semantic search:

```sql
-- Vector embeddings stored in products table
ALTER TABLE products ADD COLUMN embedding vector(384);

-- Similarity search
SELECT * FROM products 
WHERE embedding <-> query_embedding < distance_threshold
ORDER BY embedding <-> query_embedding
LIMIT 10;
```

### **Ollama Integration (E-Commerce)**

Ollama provides local LLM and embedding capabilities:

**Available Models** (auto-pulled by `ollama-init`):
- `qwen2.5:0.5b` — Fast chat model
- `nomic-embed-text` — Text embeddings for similarity

**API Usage**:

```bash
# Get available tags
curl http://localhost:11434/api/tags

# Generate embeddings
curl -X POST http://localhost:11434/api/embeddings \
  -H "Content-Type: application/json" \
  -d '{"model":"nomic-embed-text","prompt":"wireless headphones"}'

# Chat with model
curl -X POST http://localhost:11434/api/chat \
  -H "Content-Type: application/json" \
  -d '{"model":"qwen2.5:0.5b","messages":[{"role":"user","content":"Hello"}]}'
```

**Spring Boot Integration**:

```java
@Autowired
private EmbeddingClient embeddingClient;

// Generate embeddings
EmbeddingResponse response = embeddingClient.embedForResponse("query text");
```

### **Anthropic Claude Integration (E-Commerce)**

Uses Spring AI to integrate Anthropic's Claude Haiku model:

```java
@Autowired
private ChatClient chatClient;

// Generate AI content
Message response = chatClient.call(
    new Prompt("Generate a product description")
);
```

### **Docker Volume Persistence**

Data survives container restarts:

```yaml
volumes:
  postgres_data:    # PostgreSQL data
  ollama_data:      # Ollama models cache
```

To preserve data:
```bash
# Safe stop
docker-compose down

# Restart (data intact)
docker-compose up -d
```

To delete all data:
```bash
docker-compose down -v
```

---

## Author

**Rupesh Patil**

---

## License

This project is part of an AI/ML learning initiative.

---

## Quick Reference

| Command | Purpose |
|---------|---------|
| `mvn spring-boot:run` | Start Spring Boot backend locally |
| `npm start` | Start React development server (Create React App) |
| `npm run dev` | Start Vite development server |
| `docker-compose up -d` | Start all Docker services |
| `docker-compose logs -f` | Stream all logs |
| `docker-compose ps` | List running containers |
| `docker-compose down` | Stop all services |
| `docker-compose down -v` | Stop services and remove volumes |

---

**For detailed Docker setup, see [DOCKER_SETUP.md](DOCKER_SETUP.md)**
