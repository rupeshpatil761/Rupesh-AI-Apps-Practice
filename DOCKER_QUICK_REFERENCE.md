# Docker Quick Reference

## File Structure Created

```
Rupesh-AI-Apps-Practice/
├── docker-compose.yml          # Main orchestration file
├── .env.example                # Environment template
├── .env                         # Environment variables (create from example)
├── docker-start.bat            # Windows quick start script
├── init_db.sql                 # PostgreSQL initialization
├── DOCKER_SETUP.md             # Full documentation
│
├── SpringEcomBackend/
│   ├── Dockerfile              # Backend container image
│   ├── .dockerignore           # Exclude files from Docker build
│   ├── pom.xml
│   └── src/
│
└── Ecom-FrontEnd-AI/
    ├── Dockerfile              # Frontend container image
    ├── .dockerignore           # Exclude files from Docker build
    ├── package.json
    └── src/
```

## Quick Start (Windows)

### Option 1: Interactive Script
```batch
# Run from project root
docker-start.bat
```

### Option 2: Command Line
```bash
# 1. Set up environment
copy .env.example .env
# Edit .env and add ANTHROPIC_API_KEY

# 2. Start everything
docker-compose up -d

# 3. View logs
docker-compose logs -f

# 4. Access the app
# Frontend: http://localhost:3000
# Backend: http://localhost:8080
```

## Quick Start (macOS/Linux)

```bash
# Create environment file
cp .env.example .env
# Edit .env with your API key
nano .env

# Start services
docker-compose up -d

# View logs
docker-compose logs -f
```

## Essential Commands

| Command | Purpose |
|---------|---------|
| `docker-compose up -d` | Start all services in background |
| `docker-compose down` | Stop all services |
| `docker-compose logs -f` | Follow logs from all services |
| `docker-compose ps` | Show running containers |
| `docker-compose build` | Rebuild images |
| `docker-compose up -d --build` | Rebuild and start |
| `docker-compose down -v` | Stop and remove volumes (delete data) |

## View Individual Service Logs

```bash
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

## Access Database

```bash
docker-compose exec postgres psql -U postgres -d ecom-db
```

## Enter Container Shell

```bash
docker-compose exec backend /bin/sh
docker-compose exec frontend /bin/sh
```

## Services & Ports

| Service | Port | URL |
|---------|------|-----|
| Frontend (React) | 3000 | http://localhost:3000 |
| Backend (Spring Boot) | 8080 | http://localhost:8080 |
| PostgreSQL | 5432 | localhost:5432 |

## Environment Variables Required

In `.env`:
```env
ANTHROPIC_API_KEY=sk-ant-api03-your_key_here
```

## Troubleshooting

**Backend won't start**
```bash
docker-compose logs backend
docker-compose down && docker-compose up -d
```

**Port already in use**
```bash
# Change in docker-compose.yml:
# ports:
#   - "9090:8080"    # Use 9090 instead of 8080
```

**Reset everything**
```bash
docker-compose down -v
docker system prune -a
docker-compose up -d --build
```

## Development Workflow

1. Make code changes locally
2. Rebuild affected service:
   ```bash
   docker-compose up -d --build backend   # or frontend
   ```
3. View changes in browser

## Data Persistence

- **PostgreSQL data**: Stored in `postgres_data` volume
- Survives container restarts
- Deleted with: `docker-compose down -v`

## Full Documentation

See `DOCKER_SETUP.md` for comprehensive guide including:
- Detailed configuration
- Advanced troubleshooting
- Production considerations
- Performance tips
- Resource limits

