# API Blog

REST API for a blog backend built with Spring Boot, JWT authentication, PostgreSQL, MyBatis, and Pinata IPFS file uploads.

## Tech Stack

- Java 21
- Spring Boot 3.4.1
- Spring Security (JWT)
- MyBatis
- PostgreSQL
- springdoc OpenAPI (Swagger UI)
- Maven

## Features

- User registration and login with JWT token generation
- Stateless authentication with Spring Security filter chain
- Global logout by token version invalidation
- Create post with image upload support (multipart/form-data)
- Image upload to Pinata and storage of returned IPFS gateway URL
- Standardized API response format

## Project Structure

- `src/main/java/com/example/api_blog/controller` - API controllers
- `src/main/java/com/example/api_blog/service` - business logic and integrations
- `src/main/java/com/example/api_blog/repository` - MyBatis data access layer
- `src/main/resources/schema.sql` - database schema
- `src/main/resources/application.properties` - application configuration

## Additional Docs

- `README.portfolio.md` - concise project overview for GitHub visitors
- `README.onboarding.md` - detailed setup and troubleshooting guide for team members

## Prerequisites

- JDK 21+
- Maven 3.9+ (or use included Maven wrapper)
- PostgreSQL running locally (default expected: `localhost:5432`)
- Pinata account and API keys (for image upload endpoint)

## Environment Variables

Set the following environment variables before running the app:

- `DB_PASSWORD` - password for PostgreSQL user in `application.properties`
- `JWT_SECRET` - secret key used to sign JWT tokens
- `PINATA_API_KEY` - Pinata API key
- `PINATA_SECRET_KEY` - Pinata secret API key
- `PINATA_JWT` - configured in properties (currently optional in code path)

## Configuration

Default database settings from `application.properties`:

- URL: `jdbc:postgresql://localhost:5432/api_blog`
- Username: `postgres`
- Password: `${DB_PASSWORD}`

If your environment differs, update `src/main/resources/application.properties`.

## Database Setup

1. Create a PostgreSQL database named `api_blog`.
2. Run the SQL script from `src/main/resources/schema.sql`.

Example using `psql`:

```bash
psql -U postgres -d api_blog -f src/main/resources/schema.sql
```

## Run the Application

Using Maven wrapper:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

The app runs by default on `http://localhost:8080`.

## API Documentation

When the app is running:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Authentication Flow

1. Register user at `POST /api/v1/auths/register`
2. Login at `POST /api/v1/auths/login` to receive JWT token
3. Send token in Authorization header for protected endpoints:

```http
Authorization: Bearer <your_token>
```

## API Endpoints

### Auth

- `POST /api/v1/auths/register` (public)
- `POST /api/v1/auths/login` (public)
- `POST /api/v1/auths/logout-all` (authenticated)

Register request body:

```json
{
  "userName": "john",
  "email": "john@example.com",
  "password": "strong-password"
}
```

Login request body:

```json
{
  "email": "john@example.com",
  "password": "strong-password"
}
```

### Posts

- `POST /api/v1/posts/add-post` (authenticated, `multipart/form-data`)

Expected multipart fields:

- `title` (text)
- `description` (text)
- `files` (one or many file parts)

Example `curl`:

```bash
curl -X POST "http://localhost:8080/api/v1/posts/add-post" \
  -H "Authorization: Bearer <your_token>" \
  -F "title=My First Post" \
  -F "description=Post with IPFS images" \
  -F "files=@/path/to/image1.jpg" \
  -F "files=@/path/to/image2.png"
```

## Response Format

Most endpoints return the same response envelope:

```json
{
  "message": "string",
  "payload": {},
  "status": 200,
  "dateTime": "2026-05-01T20:00:00"
}
```

## Build

```bash
./mvnw clean package
```

Windows PowerShell:

```powershell
.\mvnw.cmd clean package
```
