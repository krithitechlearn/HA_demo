# Shore Excursions Application - Backend

Spring Boot 3.x backend for the Shore Excursions Application Modernization project.
Replaces the legacy IBM Domino application with a modern REST API.

## Tech Stack
- Java 21, Spring Boot 3.x
- PostgreSQL 15+, Flyway migrations
- Spring Security 6 (JWT), MapStruct, Lombok
- springdoc-openapi (Swagger UI)

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.9+
- Docker & Docker Compose

### Run with Docker Compose
```bash
cd Backend
docker-compose up -d
```

### Run locally
```bash
# Start PostgreSQL
docker run -d --name postgres -e POSTGRES_DB=shoreexcursions \
  -e POSTGRES_USER=shoreexcursions -e POSTGRES_PASSWORD=shoreexcursions \
  -p 5432:5432 postgres:15-alpine

# Set environment variables
export DATABASE_URL=jdbc:postgresql://localhost:5432/shoreexcursions
export DATABASE_USERNAME=shoreexcursions
export DATABASE_PASSWORD=shoreexcursions
export APP_JWT_SECRET=your-256-bit-secret-key-here
export APP_JWT_EXPIRATION_MS=28800000
export ALLOWED_ORIGINS=http://localhost:3000

# Build and run
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Build
```bash
mvn clean package -DskipTests
mvn clean verify  # with tests
```

## API Documentation
Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/api-docs

## Health Check
http://localhost:8080/actuator/health

## Default Admin User
- Username: `admin`
- Password: `ChangeMe@12345` (change immediately in production)
