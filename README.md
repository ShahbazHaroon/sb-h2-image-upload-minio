# sb-h2-image-upload-minio
Spring Boot project using H2 database and Swagger to upload image using MinIO

---
## Overview
This is a simple Spring Boot application using **H2 in-memory database**. It also integrates **Swagger** for API documentation and testing.

---
## Getting Started

---
## Prerequisites
Make sure you have the following installed:
- Java 17+ (for Spring Boot)
- Maven 3.8+ (Build tool for Spring Boot application)
- Docker & Docker Compose (Containerized environment)
- IDE like IntelliJ IDEA or Eclipse (optional)
- Postman or Swagger UI for API testing

---
## Tech Stack
- **Java 17+**
- **Spring Boot**
- **Spring Web**
- **Spring Data JPA**
- **H2 Database**
- **MinIO**
- **Swagger (OpenAPI)**
- **Maven**
- **Docker & Docker Compose**

---
### Clone the repository
```bash
git clone https://github.com/ShahbazHaroon/sb-h2-image-upload-minio.git
cd sb-h2-image-upload-minio
```
---
### Build the project
```bash
mvn clean install
```
---
### Run the application locally (without Docker):
After cloning the repository, navigate to the project root and run:
```bash
./mvnw spring-boot:run
# or, if Maven is installed globally
mvn spring-boot:run
```
Access: http://localhost:8080/sb-h2-image-upload-minio/swagger-ui/index.html or http://localhost:8080/sb-h2-image-upload-minio/api/v1/status
- **Note**: To store images locally without Docker, you need a running MinIO instance. You can download and run MinIO locally from https://min.io
---
### Run the application with Docker:
```dockerfile
# Build the Docker image
docker build -t sb-h2-image-upload-minio .
# Run the container
docker run --name sb-h2-image-upload-minio-container -p 8080:8080 sb-h2-image-upload-minio
```
Access: http://localhost:8080/sb-h2-image-upload-minio/swagger-ui/index.html or http://localhost:8080/sb-h2-image-upload-minio/api/v1/status

---
### Run the application with Docker Compose:
```dockerfile
docker compose up --build
# Connect to the running container
docker exec -it sb-h2--image-upload-minio-container /bin/sh
or
docker exec -it sb-h2--image-upload-minio-container bash
```
Access: http://localhost:8080/sb-h2-image-upload-minio/swagger-ui/index.html or http://localhost:8080/sb-h2-image-upload-minio/api/v1/status
MinIO (from your laptop): http://localhost:9001/login
Username/password: minioadmin:minioadmin
MinIO (from inside docker container) curl -v http://minio:9000, curl -v http://minio:9001, curl -v https://minio:9000 --insecure, curl -v https://minio:9001 --insecure
---
### Persist H2 data locally: (Optional)
If you want your H2 database file stored locally (not in-memory), change your datasource URL:
```properties
spring.datasource.url=jdbc:h2:file:/data/sb-h2-image-upload-minio-db
```
And in Docker Compose:
```yaml
volumes:
      - ./h2-data:/data
```
---
### H2 Database console
Access: http://localhost:8080/sb-h2-image-upload-minio/h2-console
```bash
jdbc:h2:mem:sb-h2-image-upload-minio-db
```
---
### Insert Sample Data
```sql
import.sql
```
---
### REST API Endpoints
- **Create new resource**
```bash
http://localhost:8080/sb-h2-image-upload-minio/api/v1/user
```
```json
{
  "idempotency_key": "IDEMP-005",
  "user_name": "addison.b",
  "email": "addison.b@example.com",
  "password": "pass123",
  "date_of_birth": "2000-01-01",
  "date_of_leaving": "2060-12-31",
  "postal_code": 56004,
  "profile_image_object_name": "MinIO-object-name",
  "profile_image_bucket": "user-images"
}
```
- **Get all resources**
```bash
http://localhost:8080/sb-h2-image-upload-minio/api/v1/user
```
- **Get resource by ID**
```bash
http://localhost:8080/sb-h2-image-upload-minio/api/v1/user/1
```
- **Update resource by ID**
```bash
http://localhost:8080/sb-h2-image-upload-minio/api/v1/user/1
```
```json
{
  "idempotency_key": "IDEMP-001",
  "user_name": "john123.doe",
  "email": "john123@example.com",
  "password": "pass123",
  "date_of_birth": "1998-02-15",
  "date_of_leaving": "2060-12-31",
  "postal_code": 56001,
  "profile_image_object_name": "MinIO-object-name",
  "profile_image_bucket": "user-images"
}
```
- **Partially update resource by ID**
```bash
http://localhost:8080/sb-h2-image-upload-minio/api/v1/user/1
```
```json
{
  "postal_code": 56000
}
```
- **Delete resource by ID**
```bash
http://localhost:8080/sb-h2-image-upload-minio/api/v1/user/1
```
- **Restore deleted resource by ID**
```bash
http://localhost:8080/sb-h2-image-upload-minio/api/v1/user/1/restore
```
- **Search with pagination**
```bash
http://localhost:8080/sb-h2-image-upload-minio/api/v1/user/search
```
- If the client sends: (PaginationService will automatically set default sort to: entity primary key)
```json
{ "page": 0, "size": 10 }
```
- Search for username/email like “john”
```json
{
  "search": "john"
}
```
```sql
SELECT * FROM USERS WHERE USER_NAME LIKE '%john%' OR EMAIL LIKE '%john%';
```
- Search Users (Basic)
```json
{
  "page": 0,
  "size": 10,
  "search": "john"
}
```
```sql
SELECT * FROM USERS WHERE USER_NAME LIKE '%john%' OR EMAIL LIKE '%john%' LIMIT 10 OFFSET 0;
```
- Filter postalCode = 560001
```json
{
  "filters": [
    {
      "field": "postalCode",
      "operator": "eq",
      "value": 560001
    }
  ]
}
```
```sql
SELECT * FROM USERS WHERE POSTAL_CODE = 560001;
```
- Search Users with Filters
```json
{
  "page": 0,
  "size": 10,
  "filters": [
    {
      "field": "postalCode",
      "operator": "eq",
      "value": 560001
    }
  ]
}
```
```sql
SELECT * FROM USERS WHERE POSTAL_CODE = 560001 LIMIT 10 OFFSET 0;
```
- Filter dateOfBirth >= 1995-01-01
```json
{
  "filters": [
    {
      "field": "dateOfBirth",
      "operator": "gte",
      "value": "1995-01-01"
    }
  ]
}
```
```sql
SELECT * FROM USERS WHERE DATE_OF_BIRTH >= '1995-01-01';
```
- Advanced Search + Filters + Sort
```json
{
  "page": 0,
  "size": 5,
  "sortBy": "userName",
  "sortDir": "asc",
  "search": "john",
  "filters": [
    {
      "field": "postalCode",
      "operator": "eq",
      "value": 560001
    },
    {
      "field": "dateOfBirth",
      "operator": "gte",
      "value": "1995-01-01"
    }
  ]
}
```
```sql
SELECT * FROM USERS WHERE POSTAL_CODE = 560001 AND DATE_OF_BIRTH >= '1995-01-01' AND USER_NAME LIKE '%john%' ORDER BY USER_NAME ASC LIMIT 5
```
- Filter userName in [john.doe, alice.wander]
```json
{
  "filters": [
    {
      "field": "userName",
      "operator": "in",
      "value": [
        "john.doe",
        "alice.wonder"
      ]
    }
  ]
}
```
```sql
SELECT * FROM USERS WHERE USER_NAME IN ('john.doe', 'alice.wonder');
```