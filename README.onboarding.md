# API Blog - Team Onboarding Guide

This guide is for developers joining the project and setting up the API locally.

## What This Service Does

- Manages user auth and JWT sessions
- Allows authenticated users to create posts
- Uploads post images to Pinata IPFS
- Stores metadata in PostgreSQL using MyBatis

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Spring Security (stateless JWT)
- MyBatis
- PostgreSQL
- Maven Wrapper
- Swagger via springdoc

## Local Setup

### 1) Prerequisites

- JDK 17+
- PostgreSQL installed and running
- Internet access to Pinata API (for image upload endpoint)

### 2) Environment Variables

Set these in your shell/user environment before running:

- `DB_PASSWORD`
- `JWT_SECRET`
- `PINATA_API_KEY`
- `PINATA_SECRET_KEY`
- `PINATA_JWT`

PowerShell example:

```powershell
$env:DB_PASSWORD="your_db_password"
$env:JWT_SECRET="a-strong-secret-key"
$env:PINATA_API_KEY="your_pinata_api_key"
$env:PINATA_SECRET_KEY="your_pinata_secret_key"
$env:PINATA_JWT="your_pinata_jwt"
```

### 3) Database

Create the `api_blog` database and run schema:

```powershell
psql -U postgres -d api_blog -f src/main/resources/schema.sql
```

The app expects:

- Host: `localhost`
- Port: `5432`
- DB: `api_blog`
- User: `postgres`

Change `src/main/resources/application.properties` if needed.

### 4) Run Application

```powershell
.\mvnw.cmd spring-boot:run
```

Default URL: `http://localhost:8080`

## API Usage

### Swagger

- UI: `http://localhost:8080/swagger-ui/index.html`
- JSON: `http://localhost:8080/v3/api-docs`

### Authentication Endpoints

- `POST /api/v1/auths/register` (public)
- `POST /api/v1/auths/login` (public)
- `POST /api/v1/auths/logout-all` (requires Bearer token)

Example login body:

```json
{
  "email": "john@example.com",
  "password": "strong-password"
}
```

### Protected Endpoint

- `POST /api/v1/posts/add-post` (multipart, authenticated)

Send header:

```http
Authorization: Bearer <token>
```

`multipart/form-data` fields:

- `title`
- `description`
- `files` (repeat for multiple files)

## Build and Test

Build JAR:

```powershell
.\mvnw.cmd clean package
```

Run tests:

```powershell
.\mvnw.cmd test
```

## Troubleshooting

- `401 Unauthorized` on protected routes:
  - Verify `Authorization: Bearer <token>` format.
  - Re-login to get a fresh token.
- DB connection errors:
  - Confirm PostgreSQL is running and `DB_PASSWORD` is set.
  - Check DB name/user in `application.properties`.
- Pinata upload failure:
  - Validate `PINATA_API_KEY` and `PINATA_SECRET_KEY`.
  - Check network access to `api.pinata.cloud`.
- Swagger not loading:
  - Confirm app started successfully on port `8080`.
  - Open `/swagger-ui/index.html` directly.

## Codebase Orientation

- `controller` package: request/response endpoints
- `service` package: business logic
- `repository` package: MyBatis persistence
- `jwt` package: token generation/filter/entrypoint
- `model` package: entity/request/response DTOs

