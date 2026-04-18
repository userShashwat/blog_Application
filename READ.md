# Blog Application using Spring Boot

Full-featured blog platform with role‑based security (Spring Security + JWT).

## ✨ Features

- User registration & login (JWT authentication)
- Role‑based access control (`ROLE_USER`, `ROLE_ADMIN`)
- Create, edit, delete blog posts
- Comment on posts
- Categorize posts
- REST API with Swagger/OpenAPI documentation

## 🛠️ Tech Stack

- Spring Boot 3.0.6
- Spring Security
- Spring Data JPA
- MySQL
- JSON Web Tokens (JJWT)
- ModelMapper
- SpringDoc OpenAPI

## 📋 Prerequisites

- Java 17 or 21 (the project compiles to Java 17 bytecode)
- MySQL server (local installation)
- Maven (or use the included Maven wrapper `./mvnw`)

## 🚀 How to Run the Project

### 1. Clone the repository

```bash
git clone https://github.com/userShashwat/blog_Application.git
cd blog_Application
```

### 2. Set up the database

Start your MySQL server. Create a database named `blog_application`:

```sql
CREATE DATABASE blog_application;
```

### 3. Configure secrets (database password & JWT secret)

> **Important:** The tracked `application.properties` contains only placeholders for security reasons. You must provide your real credentials locally using one of the methods below.

**Option A (recommended): Use a local profile**

Create a file `src/main/resources/application-local.properties` (this file is ignored by Git):

```properties
spring.datasource.password=YOUR_MYSQL_PASSWORD
app.jwt-secret=YOUR_GENERATED_JWT_SECRET
```

Generate a strong JWT secret using: `openssl rand -hex 32`

**Option B: Use environment variables**

Set the following environment variables before running:

- `DB_PASSWORD` = your MySQL password
- `JWT_SECRET` = your JWT secret

Then run normally:

```bash
./mvnw spring-boot:run
```

### 4. Run the application

After providing the secrets, start the app:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

The application will be available at http://localhost:8080

## 📚 API Documentation

- **Postman Collection:** `Blog Application.postman_collection.json`
- **API Documentation (Swagger/OpenAPI):** [Postman Documentation](https://documenter.getpostman.com)

This version improves secret management using Spring profiles and a local configuration file.

Happy coding! 😊