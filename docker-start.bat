@echo off
REM Docker Compose Quick Start Script for Windows

echo.
echo ========================================
echo Ecommerce Docker Setup
echo ========================================
echo.

REM Check if Docker is installed
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Docker is not installed or not in PATH
    echo Please install Docker from https://docs.docker.com/get-docker/
    exit /b 1
)

REM Check if .env file exists
if not exist ".env" (
    echo Creating .env file from template...
    copy ".env.example" ".env"
    echo.
    echo WARNING: Please edit .env and add your ANTHROPIC_API_KEY
    echo Opening .env for editing...
    timeout /t 3
    start notepad .env
    echo.
    echo Please save the file and restart this script.
    exit /b 1
)

REM Menu
:menu
echo.
echo Select an option:
echo 1. Start all services (docker-compose up -d)
echo 2. Stop all services (docker-compose down)
echo 3. View logs (docker-compose logs -f)
echo 4. Rebuild and start (docker-compose up -d --build)
echo 5. Stop and remove volumes (docker-compose down -v)
echo 6. Open application in browser
echo 7. Access PostgreSQL
echo 8. Exit
echo.
set /p choice="Enter your choice (1-8): "

if "%choice%"=="1" (
    echo Starting services...
    docker-compose up -d
    echo.
    echo Services started successfully!
    echo Frontend: http://localhost:3000
    echo Backend: http://localhost:8080
    goto menu
)

if "%choice%"=="2" (
    echo Stopping services...
    docker-compose down
    echo Services stopped!
    goto menu
)

if "%choice%"=="3" (
    docker-compose logs -f backend frontend postgres
    goto menu
)

if "%choice%"=="4" (
    echo Rebuilding and starting services...
    docker-compose up -d --build
    echo.
    echo Services rebuilt and started!
    goto menu
)

if "%choice%"=="5" (
    echo Stopping services and removing volumes...
    docker-compose down -v
    echo All services and data removed!
    goto menu
)

if "%choice%"=="6" (
    echo Opening http://localhost:3000...
    start http://localhost:3000
    goto menu
)

if "%choice%"=="7" (
    echo Connecting to PostgreSQL...
    docker-compose exec postgres psql -U postgres -d ecom-db
    goto menu
)

if "%choice%"=="8" (
    echo Exiting...
    exit /b 0
)

echo Invalid choice. Please try again.
goto menu

