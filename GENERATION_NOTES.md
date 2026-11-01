# GENERATION NOTES

## Coverage

| US ID | Story Title | API IDs | Generated Classes | Tests |
|-------|-------------|---------|-------------------|-------|
| US-001 | Create Port Record | API-006 | PortController, PortServiceImpl, PortRepository, PortRequestDto, PortMapper | PortServiceImplTest, PortControllerTest, PortRepositoryIT |
| US-002 | View Port Record Details | API-005 | PortController, PortServiceImpl, PortResponseDto | PortServiceImplTest, PortControllerTest |
| US-003 | Update Port Information | API-007 | PortController, PortServiceImpl | PortControllerTest |
| US-004 | Delete Port Record | API-008 | PortController, PortServiceImpl, TourRepository | PortServiceImplTest, PortControllerTest |
| US-005 | Create Tour Record | API-011 | TourController, TourServiceImpl, TourRepository, TourRequestDto, TourMapper | TourServiceImplTest, TourControllerTest, TourRepositoryIT |
| US-006 | View Tour Record Details | API-010 | TourController, TourServiceImpl, TourResponseDto | TourServiceImplTest, TourControllerTest |
| US-007 | Update Tour Information | API-012 | TourController, TourServiceImpl | TourServiceImplTest, TourControllerTest |
| US-008 | Delete Tour Record | API-013 | TourController, TourServiceImpl | TourServiceImplTest, TourControllerTest |
| US-009 | View All Ports | API-004 | PortController, PortServiceImpl, PortSummaryDto | PortServiceImplTest, PortControllerTest |
| US-010 | View Tours by Port | API-009, API-014 | TourController, TourServiceImpl | TourServiceImplTest, TourControllerTest |
| US-011 | Retrieve All Ports via API | API-004 | PortController, PortServiceImpl | PortControllerTest |
| US-012 | Retrieve Specific Port via API | API-005 | PortController, PortServiceImpl | PortControllerTest |
| US-013 | Create Port via API | API-006 | PortController, PortServiceImpl | PortControllerTest |
| US-014 | Update Port via API | API-007 | PortController, PortServiceImpl | PortControllerTest |
| US-015 | Retrieve All Tours via API | API-009 | TourController, TourServiceImpl | TourControllerTest |
| US-016 | Retrieve Tours by Port via API | API-014 | TourController, TourServiceImpl | TourControllerTest |
| US-017 | Retrieve Specific Tour via API | API-010 | TourController, TourServiceImpl | TourControllerTest |
| US-018 | Create Tour via API | API-011 | TourController, TourServiceImpl | TourControllerTest |
| US-019 | Update Tour via API | API-012 | TourController, TourServiceImpl | TourControllerTest |
| US-020 | API Error Handling | All APIs | GlobalExceptionHandler, ErrorResponse | PortControllerTest, TourControllerTest, AuthControllerTest |
| US-024 | Port Code Uniqueness | API-006 | PortServiceImpl, PortRepository | PortServiceImplTest, PortControllerTest, PortRepositoryIT |
| US-025 | Tour ID Uniqueness | API-011 | TourServiceImpl | TourServiceImplTest |
| US-026 | Port-Tour FK Validation | API-011, API-012 | TourServiceImpl, PortRepository | TourServiceImplTest, TourRepositoryIT |
| US-027 | Required Field Validation | API-006, API-011 | PortRequestDto, TourRequestDto (Bean Validation) | PortControllerTest, TourControllerTest |
| US-031 | Image Upload | API-015-018 | ImageController, LocalImageStorageServiceImpl, PortImageRepository, TourImageRepository | - |
| US-037 | User Authentication | API-001 | AuthController, AuthServiceImpl, JwtTokenProvider, JwtAuthenticationFilter | AuthServiceImplTest, AuthControllerTest, JwtTokenProviderTest |
| US-038 | Role-Based Access Control | API-019-024 | UserController, UserServiceImpl, SecurityConfig | AuthControllerTest |
| US-039 | DB Admin Role Permissions | All APIs | SecurityConfig, @PreAuthorize | PortControllerTest, TourControllerTest |
| US-040 | Editor Role Permissions | API-006,007,011,012 | SecurityConfig, @PreAuthorize | PortControllerTest, TourControllerTest |
| US-041 | User Role Permissions | API-004,005,009,010 | SecurityConfig | PortControllerTest |
| US-042 | Viewer Role Permissions | API-004,005,009,010 | SecurityConfig | PortControllerTest |
| US-043 | Endpoint-Level Security | All APIs | JwtAuthenticationFilter, SecurityConfig | AuthControllerTest |
| US-044 | Record-Level Security | All APIs | SecurityConfig, @PreAuthorize | PortControllerTest |
| US-045 | Audit Trail | API-025 | AuditServiceImpl, AuditLogRepository, AuditController | AuditServiceImpl (via service tests) |
| US-046 | Secure Password Reset | API-002, API-003 | AuthController, AuthServiceImpl | AuthControllerTest |
| US-047 | Data Migration | - | V1__init_schema.sql, V3__legacy_role_mapping.sql | PortRepositoryIT, TourRepositoryIT |
| US-048 | Legacy Role Mapping | API-023 | UserServiceImpl, V3__legacy_role_mapping.sql | - |
| US-049 | API Documentation | All APIs | OpenApiConfig, springdoc annotations | - |
| US-051 | API Response Time | All APIs | Indexes, @Transactional(readOnly), pagination | - |
| US-053 | DB Performance | All APIs | Indexes in V1__init_schema.sql | - |
| US-055 | Health Monitoring | API-026, API-027 | Spring Actuator config | - |
| US-056 | Error Logging | - | GlobalExceptionHandler, TraceIdFilter, logback-spring.xml | - |
| US-059 | Cloud Infrastructure | - | Dockerfile | - |
| US-060 | Containerization | - | Dockerfile, docker-compose.yml | - |

## Deviations and Gaps

| # | LLD Section | Deviation | Reason |
|---|-------------|-----------|--------|
| 1 | 6.3 AuthServiceImpl.resetPassword | Password reset token storage not implemented (stub throws TOKEN_EXPIRED) | LLD A-08 explicitly defers email service and token management. Stub interface defined for future implementation. |
| 2 | 9 (Rate Limiting) | RateLimitingFilter uses in-memory ConcurrentHashMap (not distributed) | LLD A-07 defers distributed caching. In-memory is safe for single-instance deployment. |
| 3 | 5.3 (Seed Data) | V2 migration uses a hardcoded BCrypt hash placeholder | Real password must be injected via environment variable at deployment time. Documented in README. |
| 4 | 6.5 (Image validation) | Image content-type validation done in service layer, not via custom @ContentType annotation | Custom annotation would require additional library. Service-layer validation achieves same result. |
| 5 | 6.2 (PortMapper.tourCount) | tourCount is not mapped by MapStruct (set manually in service) | tourCount is a computed value from a separate query, not a direct field on Port entity. MapStruct cannot map it automatically. |
| 6 | 6.2 (TourResponseDto.portName) | portName is not mapped by MapStruct (set manually in service) | portName comes from a separate Port entity lookup, not a field on Tour. |
| 7 | 11 (Frontend tests) | Frontend tests not generated | LLD Section 7 (frontend) is explicitly excluded per task instructions. |
| 8 | 11 (E2E Cypress) | Cypress E2E tests not generated | LLD Section 11 explicitly defers Cypress to v1.1. |
| 9 | 12 (US-021, US-022, US-023) | Request/approval workflow not implemented | LLD A-08, A-09 explicitly defer these. No Request entity modeled. |
| 10 | 12 (US-054) | No Redis/cache library added | LLD A-07 explicitly defers caching. HTTP Cache-Control headers applied on GET endpoints. |
| 11 | 9 (SecurityConfig DELETE) | DELETE /api/v1/ports and /api/v1/tours use @PreAuthorize at method level | URL-level rules in SecurityConfig handle the general pattern; method-level @PreAuthorize provides fine-grained control per LLD role matrix. |

## Assumptions Applied

| ID | How Applied |
|----|-------------|
| A-01 | JWT Bearer tokens, 8-hour expiry, stateless Spring Security config |
| A-02 | BCrypt strength 12 in SecurityConfig.passwordEncoder() |
| A-03 | portCode is user-entered VARCHAR(10) uppercase; tourId is UUID auto-generated |
| A-04 | Images stored as URL strings; LocalImageStorageServiceImpl builds URL from base-url config |
| A-05 | Default page size 20, max 100 enforced in all controllers |
| A-06 | AuditableEntity provides created_at/updated_at/created_by/updated_by; AuditServiceImpl logs to audit_log table |
| A-07 | Cache-Control headers added to GET /ports and GET /tours responses |
| A-08 | AuthServiceImpl.requestPasswordReset is a stub; resetPassword throws TOKEN_EXPIRED |
| A-09 | No Request entity; US-021/US-022 deferred |
| A-10 | No row-level ownership filtering; all authenticated users see all ports/tours |
| A-11 | V3__legacy_role_mapping.sql creates reference table with Domino-to-RBAC mapping |
| A-12 | ActivityLevel enum: LOW, MODERATE, HIGH, EXTREME |
| A-13 | App runs on HTTP; TLS at load balancer |
| A-14 | Dockerfile provided for Spring Boot app |
| Q-01 | portCode is user-entered (max 10 chars, uppercase alphanumeric) |
| Q-02 | Max 10 images per entity enforced in LocalImageStorageServiceImpl |
| Q-03 | region is free-text VARCHAR(100) |
| Q-04 | Hard-delete with audit log entry |
| Q-05 | Local database authentication for v1 |
| Q-06 | Provider-agnostic image URL via IMAGE_STORAGE_BASE_URL env var |

## Configuration Keys

### Environment Variables
| Variable | Purpose |
|----------|---------|
| `DATABASE_URL` | PostgreSQL JDBC URL |
| `DATABASE_USERNAME` | DB username |
| `DATABASE_PASSWORD` | DB password |
| `APP_JWT_SECRET` | JWT signing secret (min 256-bit) |
| `APP_JWT_EXPIRATION_MS` | JWT token lifetime in milliseconds (default 28800000 = 8h) |
| `ALLOWED_ORIGINS` | Comma-separated CORS allowed origins |
| `IMAGE_STORAGE_BASE_URL` | Base URL for object storage |
| `IMAGE_STORAGE_BUCKET` | Object storage bucket name |
| `IMAGE_STORAGE_ACCESS_KEY` | Object storage access key |
| `IMAGE_STORAGE_SECRET_KEY` | Object storage secret key |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile (dev/prod) |
| `SERVER_PORT` | Application port (default 8080) |
| `HIKARI_MAX_POOL_SIZE` | HikariCP max pool size (default 10) |

### Application Properties
| Property | Purpose |
|----------|---------|
| `app.jwt.secret` | JWT secret (from APP_JWT_SECRET) |
| `app.jwt.expiration-ms` | JWT expiry (from APP_JWT_EXPIRATION_MS) |
| `app.cors.allowed-origins` | CORS origins (from ALLOWED_ORIGINS) |
| `app.image.storage.base-url` | Image storage base URL |
| `app.image.storage.bucket` | Image storage bucket |
| `app.image.max-per-entity` | Max images per entity (default 10) |
| `spring.datasource.hikari.maximum-pool-size` | DB connection pool size |
| `spring.servlet.multipart.max-file-size` | Max upload file size (5MB) |
| `management.endpoints.web.exposure.include` | Actuator endpoints exposed |

## How to Build and Run

### Prerequisites
- Java 21+
- Maven 3.9+
- Docker (for PostgreSQL and Testcontainers)

### Build
```bash
cd Backend
mvn clean package -DskipTests
```

### Run Tests
```bash
# Unit tests only
mvn test

# All tests including integration (requires Docker for Testcontainers)
mvn verify
```

### Run Locally
```bash
# Start PostgreSQL via Docker
docker run -d --name postgres \
  -e POSTGRES_DB=shoreexcursions \
  -e POSTGRES_USER=shoreexcursions \
  -e POSTGRES_PASSWORD=shoreexcursions \
  -p 5432:5432 postgres:15-alpine

# Export required environment variables
export DATABASE_URL=jdbc:postgresql://localhost:5432/shoreexcursions
export DATABASE_USERNAME=shoreexcursions
export DATABASE_PASSWORD=shoreexcursions
export APP_JWT_SECRET=your-minimum-256-bit-secret-key-here-change-in-prod
export APP_JWT_EXPIRATION_MS=28800000
export ALLOWED_ORIGINS=http://localhost:3000
export IMAGE_STORAGE_BASE_URL=http://localhost:9000
export IMAGE_STORAGE_BUCKET=shore-excursions
export IMAGE_STORAGE_ACCESS_KEY=minioadmin
export IMAGE_STORAGE_SECRET_KEY=minioadmin

# Run with dev profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Run with Docker Compose
```bash
cd Backend
docker-compose up --build
```

### Access
- API: http://localhost:8080/api/v1
- Swagger UI: http://localhost:8080/swagger-ui.html
- Health: http://localhost:8080/actuator/health

## Not Generated

1. **Frontend (React/TypeScript)** - LLD Section 7 is explicitly excluded per task instructions.
2. **Cypress E2E tests** - Deferred to v1.1 per LLD Section 11.
3. **CI/CD pipeline** - Out of application code scope per LLD Section 2 (Out of Scope).
4. **Kubernetes/ECS manifests** - Out of scope per LLD A-14.
5. **Cloud infrastructure scripts** - Out of scope per LLD Section 2.
6. **Email notification service** - Deferred per LLD A-08.
7. **Approver workflow engine** - Deferred per LLD A-09.
8. **Request entity (US-021, US-022)** - Deferred per LLD A-09.
9. **Distributed caching (US-054)** - Deferred per LLD A-07.
10. **Automated backup orchestration (US-057)** - Delegated to cloud DB service per LLD Section 2.
