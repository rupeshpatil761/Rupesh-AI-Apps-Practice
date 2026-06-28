# Docker Setup Guide - Ecommerce Application

This guide explains how to run the entire Ecommerce application (Spring Boot backend, React frontend, PostgreSQL with pgvector, and Ollama) using Docker and Docker Compose.

## Prerequisites

- **Docker**: [Install Docker](https://docs.docker.com/get-docker/)
- **Docker Compose**: Included with Docker Desktop (Mac/Windows) or install separately on Linux
- **Anthropic API Key**: Required for AI features

Verify installation:
```bash
docker --version
docker-compose --version
```

## Quick Start

### 1. Set up Environment Variables

Copy the example `.env` file and add your Anthropic API key:

```bash
cp .env.example .env
```

Edit `.env` and replace the placeholder:
```env
ANTHROPIC_API_KEY=sk-ant-api03-your_actual_key_here
```

### 2. Start All Services

From the `Rupesh-AI-Apps-Practice` directory, run:

```bash
docker-compose up -d
```

This will:
- Build the Spring Boot backend Docker image
- Build the React frontend Docker image
- Start PostgreSQL with pgvector (with data persistence)
- Start Ollama (local LLM + embeddings API)
- Pull Ollama models via `ollama-init`
- Start the backend service (port 8080)
- Start the frontend service (port 3000)

**Expected output:**
```
✓ Network ecom_network Created
✓ Volume postgres_data Created
✓ Volume ollama_data Created
✓ Container ollama Created
✓ Container ollama-init Created
✓ Container ecom_postgres Created
✓ Container ecom_backend Created
✓ Container ecom_frontend Created
```

### 3. Access the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **PostgreSQL (pgvector)**: localhost:5432
- **Ollama API**: http://localhost:11434

### 4. Monitor Logs

View logs from all services:
```bash
docker-compose logs -f
```

View logs from a specific service:
```bash
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
docker-compose logs -f ollama
docker-compose logs -f ollama-init
```

---

## Detailed Configuration

### Backend Service (Spring Boot)

**Port**: 8080

**Environment Variables** (set in `docker-compose.yml`):
- `SPRING_DATASOURCE_URL`: PostgreSQL connection (docker service name: `postgres`)
- `SPRING_DATASOURCE_USERNAME`: postgres
- `SPRING_DATASOURCE_PASSWORD`: postgres
- `SPRING_JPA_HIBERNATE_DDL_AUTO`: update (auto-creates/updates tables)
- `SPRING_AI_ANTHROPIC_API_KEY`: Your Anthropic key (from `.env`)

**Health Check**: 
- Waits for PostgreSQL to be ready before starting
- Automatically creates/updates database schema

### Frontend Service (React)

**Port**: 3000

**Build Args**:
- `REACT_BASE_URL`: API base URL (http://localhost:8080)

**Note**: Frontend is built as static files and served by `serve` package for fast performance.

### PostgreSQL Service

**Port**: 5432
**Username**: postgres
**Password**: postgres
**Database**: ecom-db

**Data Persistence**:
- Database data is stored in `postgres_data` Docker volume
- Survives container restarts
- Delete with: `docker volume rm postgres_data`

### Ollama Service

**Port**: 11434

**Purpose**:
- Serves local chat models and embedding models
- Used for RAG pipelines without external API cost

**Model Persistence**:
- Ollama models are stored in `ollama_data` Docker volume
- Survive container restarts/redeploys

**Auto Model Pull**:
- `ollama-init` waits for Ollama to be ready
- Pulls models such as `qwen2.5:0.5b` and `nomic-embed-text`
- Can be rerun safely if models are already present

---

## Common Commands

### Stop All Services

```bash
docker-compose down
```

### Stop and Remove All Data

```bash
docker-compose down -v
```

### Rebuild Images (after code changes)

```bash
docker-compose up -d --build
```

### Rebuild Specific Service

```bash
docker-compose up -d --build backend
docker-compose up -d --build frontend
docker-compose up -d --build ollama
```

### Pull / Verify Ollama Models

```bash
docker compose exec ollama ollama pull qwen2.5:0.5b
docker compose exec ollama ollama pull nomic-embed-text
docker compose exec ollama ollama list
```

### Run Ollama Init Pull Service

```bash
docker compose up -d ollama
docker compose run --rm ollama-init
```

### Access Container Shell

```bash
# Backend
docker-compose exec backend /bin/sh

# Frontend
docker-compose exec frontend /bin/sh

# PostgreSQL
docker-compose exec postgres psql -U postgres -d ecom-db
```

### View Container Logs with Timestamps

```bash
docker-compose logs -f --timestamps backend
```

### Test Ollama Embeddings API (PowerShell)

```powershell
Invoke-RestMethod -Method Get -Uri "http://localhost:11434/api/tags"

$headers = @{ "Content-Type" = "application/json" }
$body = '{"model":"nomic-embed-text","prompt":"wireless bluetooth headphones"}'
Invoke-RestMethod -Method Post -Uri "http://localhost:11434/api/embeddings" -Headers $headers -Body $body
```

### Test Ollama Embeddings API (CMD)

```cmd
curl -X GET http://localhost:11434/api/tags
curl -X POST http://localhost:11434/api/embeddings -H "Content-Type: application/json" -d "{\"model\":\"nomic-embed-text\",\"prompt\":\"wireless bluetooth headphones\"}"
```

---

## Troubleshooting

### Backend won't start - "Connection refused"

**Cause**: Backend tried to start before PostgreSQL was ready.

**Solution**:
```bash
docker-compose down
docker-compose up -d
```

### Port already in use (e.g., 8080, 3000)

**Solution 1**: Change ports in `docker-compose.yml`:
```yaml
backend:
  ports:
    - "9090:8080"  # Access backend at 9090 instead
```

**Solution 2**: Stop other services using those ports:
```bash
# macOS/Linux
lsof -i :8080
kill -9 <PID>

# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Database connection issues

Check PostgreSQL logs:
```bash
docker-compose logs postgres
```

Verify connection from backend:
```bash
docker-compose exec backend curl -s http://postgres:5432
```

### Frontend shows "Cannot reach backend"

**Cause**: `REACT_BASE_URL` not set correctly or backend not ready.

**Solution**:
1. Ensure backend is running: `docker-compose logs backend`
2. Check frontend environment: `docker-compose exec frontend env | grep REACT`
3. Rebuild with correct base URL:
   ```bash
   docker-compose up -d --build frontend
   ```

### Ollama model not found

**Example error**: `model "nomic-embed-text" not found, try pulling it first`

**Solution**:
```bash
docker compose run --rm ollama-init
docker compose exec ollama ollama list
```

If still missing:
```bash
docker compose exec ollama ollama pull nomic-embed-text
```

### PowerShell curl command fails with headers error

**Cause**: PowerShell aliases `curl` to `Invoke-WebRequest`, which does not accept Linux-style `-H`/`-d` flags the same way.

**Solution**:
- Use `Invoke-RestMethod` (recommended on PowerShell), or
- Use `curl.exe` instead of `curl`.

### Rebuild from scratch (clear all)

```bash
docker-compose down -v
docker system prune -a
docker-compose up -d --build
```

---

## Docker Compose File Structure

```yaml
services:
  ollama:           # Local LLM/embeddings API (port 11434)
  ollama-init:      # Pulls required models on startup
  postgres:         # PostgreSQL + pgvector database
  backend:          # Spring Boot API (port 8080)
  frontend:         # React app (port 3000)

volumes:
  postgres_data:    # Persistent PostgreSQL data
  ollama_data:      # Persistent Ollama model cache

networks:
  ecom_network:     # Internal Docker network for service communication
```

---

## Performance Tips

1. **First run takes longer** (downloading images, building, database initialization)
2. **Cached layers**: Subsequent runs are much faster
3. **Hot reload**: Modify code and rebuild specific services:
   ```bash
   docker-compose up -d --build backend
   ```
4. **Resource limits**: Edit `docker-compose.yml` to add resource constraints:
   ```yaml
   backend:
     deploy:
       resources:
         limits:
           cpus: '0.5'
           memory: 512M
   ```

---

## Production Considerations

For production deployment:
1. Use `.env` for secrets (not hardcoded)
2. Set `SPRING_JPA_HIBERNATE_DDL_AUTO: validate` (don't auto-update schema)
3. Add reverse proxy (nginx)
4. Enable PostgreSQL backups
5. Use container orchestration (Kubernetes)
6. Add health checks and monitoring

---

## Support

For issues:
1. Check logs: `docker-compose logs -f`
2. Verify services running: `docker-compose ps`
3. Rebuild if needed: `docker-compose up -d --build`


