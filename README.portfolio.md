# API Blog

Backend REST API for a blog platform built with Spring Boot, JWT auth, PostgreSQL, MyBatis, and IPFS image upload via Pinata.

## Highlights

- Secure authentication with JWT (`register`, `login`, `logout-all`)
- Protected post creation endpoint with multipart image upload
- Images uploaded to Pinata and returned as IPFS gateway URLs
- Standardized API response envelope for all endpoints
- Swagger/OpenAPI docs for quick testing

## Stack

- Java 17
- Spring Boot 3.2.5
- Spring Security
- MyBatis
- PostgreSQL
- Maven
- Swagger UI (springdoc-openapi)

## Quick Start

1. Configure env vars:
   - `DB_PASSWORD`
   - `JWT_SECRET`
   - `PINATA_API_KEY`
   - `PINATA_SECRET_KEY`
   - `PINATA_JWT`
2. Create database `api_blog` and run:
   - `src/main/resources/schema.sql`
3. Start app:
   - Windows: `.\mvnw.cmd spring-boot:run`
   - Unix/macOS: `./mvnw spring-boot:run`

## API Docs

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI: `http://localhost:8080/v3/api-docs`

## Core Endpoints

- `POST /api/v1/auths/register`
- `POST /api/v1/auths/login`
- `POST /api/v1/auths/logout-all` (Bearer token required)
- `POST /api/v1/posts/add-post` (Bearer token + multipart files)

## Example Auth Header

```http
Authorization: Bearer <jwt_token>
```

