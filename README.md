# user-cache-service

A global user management service built with **Java Spring Boot**, **PostgreSQL**, and **Redis**, designed as a reusable engine for user CRUD operations across multiple projects.

## Tech Stack

| Layer        | Technology                  |
|--------------|-----------------------------|
| Framework    | Spring Boot 4.0.3 (Java 21) |
| Database     | PostgreSQL latest           |
| Cache        | Redis latest                |
| Container    | Docker + Docker Compose     |
| API Docs     | Swagger / OpenAPI 3         |
| Security     | BCrypt password hashing     |

---

## Architecture

```
Client
  │
  ▼
UserController          ← REST API layer
  │
  ▼
UserServiceImpl         ← Business logic + Cache annotations
  │           │
  ▼           ▼
UserRepository       Redis (Write-through)
(PostgreSQL)         @CachePut  / @CacheEvict
```

### Cache Strategy: Write-Through

| Operation | DB     | Redis               |
|-----------|--------|---------------------|
| Create    | INSERT | @CachePut (id + username) |
| Read      | SELECT (fallback) | @Cacheable  |
| Update    | UPDATE | @CachePut (id + username) |
| Delete    | DELETE | @CacheEvict (id + username) |

TTL: **10 minutes** (configurable via `application.yaml`)

---

## API Endpoints

### Users — `/api/v1/users`

| Method | Path              | Description               |
|--------|-------------------|---------------------------|
| POST   | `/`               | Create a new user         |
| GET    | `/{id}`           | Get user by UUID          |
| GET    | `/username/{u}`   | Get user by username      |
| GET    | `/`               | Get all users             |
| PUT    | `/{id}`           | Update user               |
| DELETE | `/{id}`           | Delete user               |

### Cache — `/api/v1/cache`

| Method | Path                        | Description                 |
|--------|-----------------------------|-----------------------------|
| GET    | `/stats`                    | View all cache keys & counts|
| DELETE | `/evict/all`                | Clear all cache             |
| DELETE | `/evict/{cacheName}/{key}`  | Evict a specific key        |

### Health — `/api/v1/health`

| Method | Path   | Description                            |
|--------|--------|----------------------------------------|
| GET    | `/`    | Returns status of app, Redis, Postgres |

---

## Running Locally

### Prerequisites
- Docker + Docker Compose

### Start everything

```bash
docker-compose up --build
```

### Access

| Resource    | URL                                      |
|-------------|------------------------------------------|
| API         | http://localhost:8080/api/v1/users       |
| Swagger UI  | http://localhost:8080/swagger-ui.html    |
| Health      | http://localhost:8080/api/v1/health      |
| Cache Stats | http://localhost:8080/api/v1/cache/stats |

---

## Environment Variables

| Variable        | Default        | Description             |
|-----------------|----------------|-------------------------|
| `DB_HOST`       | `localhost`    | PostgreSQL host         |
| `DB_PORT`       | `5432`         | PostgreSQL port         |
| `DB_NAME`       | `user_cache_db`| Database name           |
| `DB_USERNAME`   | `postgres`     | DB username             |
| `DB_PASSWORD`   | `postgres`     | DB password             |
| `REDIS_HOST`    | `localhost`    | Redis host              |
| `REDIS_PORT`    | `6379`         | Redis port              |
| `REDIS_PASSWORD`| *(empty)*      | Redis password          |
| `SERVER_PORT`   | `8080`         | App port                |

---

## Sample Requests

### Create User
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{"username":"john","email":"john@example.com","password":"secret123"}'
```

### Get User
```bash
curl http://localhost:8080/api/v1/users/{id}
```

### Update User
```bash
curl -X PUT http://localhost:8080/api/v1/users/{id} \
  -H "Content-Type: application/json" \
  -d '{"username":"john_updated"}'
```

### Delete User
```bash
curl -X DELETE http://localhost:8080/api/v1/users/{id}
```

---

## Running Tests

```bash
./gradlew test
```

---

## Project Structure

```
src/
└── main/java/usercacheservice/
    ├── config/
    │   ├── RedisConfig.java        # Cache manager + RedisTemplate
    │   ├── SecurityConfig.java     # BCrypt + HTTP security
    │   └── SwaggerConfig.java      # OpenAPI setup
    ├── controller/
    │   ├── UserController.java
    │   ├── CacheManagementController.java
    │   └── HealthController.java
    ├── domain/
    │   └── User.java               # JPA entity
    ├── dto/
    │   ├── UserDto.java            # Request/Response DTOs
    │   └── UserMapper.java
    ├── exception/
    │   ├── ResourceNotFoundException.java
    │   ├── ConflictException.java
    │   └── GlobalExceptionHandler.java
    ├── repository/
    │   └── UserRepository.java
    └── service/
        ├── UserService.java
        ├── UserServiceImpl.java    # Write-through cache logic
        └── CacheManagementService.java
```