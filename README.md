# Home Cloud Storage

A Spring Boot REST API for a personal cloud storage application with JWT authentication.

## Tech Stack

- Java 17
- Spring Boot 4.0.0
- Spring Security with JWT
- PostgreSQL
- MinIO (S3-compatible object storage)

## Getting Started

### Prerequisites

- Java 17+
- PostgreSQL
- MinIO Server

### Configuration

Configure your `application.properties` or `application.yml` with database and MinIO credentials.

### Build & Run

```bash
./mvnw spring-boot:run
```

## API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/auth/login` | User login, returns JWT token | No |
| POST | `/auth/register` | Register new user account | No |

**Login Request:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Register Request:**
```json
{
  "username": "string",
  "password": "string"
}
```

### Files

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/cloud/files/upload` | Upload a file | Yes |
| POST | `/cloud/files/list` | List files in a folder | Yes |
| GET | `/cloud/files/list/dir` | List all directories | Yes |
| GET | `/cloud/files/download/{filename}` | Download a file | Yes |
| POST | `/cloud/files/rename` | Rename a file | Yes |
| DELETE | `/cloud/files/delete` | Delete a file | Yes |
| DELETE | `/cloud/files/del` | Delete items (files/folders) | Yes |

### Folders

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/cloud/create/folder` | Create a new folder | Yes |
| POST | `/cloud/delete/folder` | Delete a folder | Yes |
| POST | `/cloud/list-folders` | List all folders | Yes |

### Account

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/cloud/auth/user/list` | Get current user info | Yes |

### Bucket

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/bucket/create/bucket` | Create an S3 bucket | Yes |
| POST | `/bucket/id` | Get bucket information | Yes |

## Authentication

All endpoints except `/auth/login` and `/auth/register` require JWT authentication.

Include the JWT token in the `Authorization` header:
```
Authorization: Bearer <your-jwt-token>
```

## CORS

Configured to allow requests from: `http://localhost:4200`
