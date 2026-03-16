# User Service — Authentication & Identity (v3)

> Cloud-native authentication microservice responsible for user registration, login, JWT access token generation, refresh token lifecycle, logout/token revocation, and role-based access control.

![Java](https://img.shields.io/badge/Java-17+-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-success)
![Architecture](https://img.shields.io/badge/Architecture-Microservices-blue)
![Tests](https://img.shields.io/badge/Tests-Repository%20%7C%20Unit%20%7C%20Slice%20%7C%20Integration-informational)
![Observability](https://img.shields.io/badge/Observability-OpenTelemetry-purple)
![Docker](https://img.shields.io/badge/Docker-Supported-2496ED)

---

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Architecture Role](#architecture-role)
- [Authentication Flow](#authentication-flow)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Domain Model](#domain-model)
- [API Endpoints](#api-endpoints)
- [Validation \& Error Handling](#validation--error-handling)
- [Security Design](#security-design)
- [Configuration](#configuration)
- [Run Locally](#run-locally)
- [Run with Docker](#run-with-docker)
- [Testing Strategy](#testing-strategy)
- [Observability](#observability)
- [Version History](#version-history)
- [Roadmap](#roadmap)
- [Author](#author)

---

## Overview

The **User Service** is the authentication and identity provider of the platform.

It centralizes:

- user signup
- login
- JWT access token generation
- refresh token management
- logout / token revocation
- role assignment
- credential validation
- secure password storage

This microservice is part of **Cloud Native Architecture v3** and is designed to integrate with:

- **Config Server**
- **Service Discovery**
- **API Gateway**
- **Relational Database**
- **Observability stack**

Its main goal is to provide a **secure, centralized, and scalable authentication layer** for the entire microservice ecosystem.

---

## Key Features

- User signup with role assignment
- Login with access token and refresh token generation
- Refresh token flow
- Logout with token revocation support
- JWT generation based on `UserDetails`
- Password encryption with BCrypt
- Validation for unique login and email
- Custom business exceptions
- Role-based security model with `Role_Enum`
- Builder-based user entity construction
- Repository, unit, slice, and integration test coverage
- Dockerized execution support

---

## Architecture Role

This service acts as the **Identity Provider** of the platform.

### Responsibilities

- Register users
- Authenticate credentials
- Generate access tokens
- Manage refresh tokens
- Revoke tokens on logout
- Expose identity and roles to the platform
- Support secure routing through the API Gateway

### Position in the system

```text
Client
  ↓
API Gateway
  ↓
User Service
  ↓
Database
````

The API Gateway uses the access token issued by this service to secure downstream microservices.

---

## Authentication Flow

```text
Client
  ↓ (signup / login)
User Service
  ↓ (access token + refresh token)
Client
  ↓ (access token)
API Gateway
  ↓
Other Microservices
```

### Token Model

#### Access Token

* JWT-based
* short-lived
* contains authenticated user identity and authorities
* sent in the `Authorization` header

#### Refresh Token

* long-lived
* used to generate a new access token
* only exchanged with the User Service
* can be revoked on logout

---

## Tech Stack

* **Java 17+**
* **Spring Boot 3.x**
* **Spring Security**
* **Spring Data JPA**
* **JWT**
* **Maven**
* **Relational Database** (PostgreSQL / MySQL depending on deployment)
* **Spring Cloud Config**
* **Service Discovery**
* **JUnit 5**
* **Mockito**
* **MockMvc**
* **spring-security-test**
* **jackson-databind**
* **Docker**

---

## Project Structure

```text
src
 ├── main
 │   ├── java/com/microtest/UserService
 │   │   ├── config
 │   │   │   └── jwt
 │   │   ├── controller
 │   │   ├── dto
 │   │   ├── entity
 │   │   ├── enums
 │   │   ├── exception
 │   │   ├── repository
 │   │   └── service
 │   │       └── impl 
 │   └── resources
 │       ├── application.yml
 │       └── bootstrap.yaml
 │
 └── test
     ├── java/com/microtest/UserService
     │   ├── controller
     │   ├── integration
     │   ├── repository
     │   ├── service
     │   └── support
     └── resources
         └── application-test.yml
```

---

## Domain Model

### User Entity

A user contains:

* login
* email
* encrypted password
* assigned role
* account-related metadata

### Role Enum

The signup contract now uses an enum instead of an integer.

```java
public enum RoleEnum {
    ROLE_USER,
    ROLE_ADMIN
}
```

This improves type safety and readability across the codebase.

### Builder Pattern

The `Users` entity now uses the **Builder pattern** for object construction.

Example:

```java
Users user = Users.builder()
    .login(request.getLogin())
    .email(request.getEmail())
    .password(encodedPassword)
    .role(request.getRole())
    .build();
```

Benefits:

* cleaner construction
* better maintainability
* safer domain creation
* easier unit testing

---

## API Endpoints

### Signup

```http
POST /auth/signup
```

Creates a new user account.

#### Request

```json
{
  "login": "john",
  "email": "john@email.com",
  "password": "123456",
  "role": "ROLE_USER" || 0-1
}
```

#### Success Response

```json
{
  "id": 1,
  "login": "john",
  "email": "john@email.com",
  "role": "ROLE_USER"
}
```

---

### Login

```http
POST /auth/login
```

Authenticates a user and generates a new access token and refresh token.

#### Request

```json
{
  "login": "john",
  "password": "123456"
}
```

#### Response

```json
{
  "accessToken": "jwt-token",
  "refreshToken": "refresh-token",
  "expiresIn": 900
}
```

---

### Refresh Token

```http
POST /auth/refresh
```

Generates a new access token using a valid refresh token.

#### Request

```json
{
  "refreshToken": "refresh-token"
}
```

#### Response

```json
{
  "accessToken": "new-jwt-token",
  "refreshToken": "new-refresh-token",
  "expiresIn": 900
}
```

---

### Logout / Token Revocation

```http
POST /auth/logout
```

Invalidates or revokes the refresh token so it can no longer be used to generate a new access token.

#### Request

```json
{
  "refreshToken": "refresh-token"
}
```

#### Response

```json
{
  "message": "Logout successful"
}
```

This ensures that a user session can be terminated cleanly from the authentication service.

---

## Validation & Error Handling

The service validates:

* duplicate login
* duplicate email
* password minimum length

### Custom Exceptions

#### `LoginOrEmailExistException`

Thrown when a user tries to register with an existing login or email.

#### `IllegalArgumentException`

Thrown when the password is shorter than the allowed minimum length.

### Example Error Response

```json
{
  "error": "Login or email already exists"
}
```

Controller behavior has been improved to return clearer error responses during signup and authentication failures.

---

## Security Design

### Password Security

* Passwords are stored using **BCrypt**

### JWT Security

* Access token generation now uses **`UserDetails`**
* Roles / authorities are embedded inside the token
* Tokens are validated by the API Gateway before request forwarding

### Refresh Token Security

* Refresh tokens are issued during login
* Refresh tokens are used only with the User Service
* Refresh tokens can be revoked on logout
* Revoked tokens should no longer be accepted for refresh operations

### Role-Based Access Control

JWT payload includes authorities, enabling route-level authorization in the gateway and downstream services.

Example token payload:

```json
{
  "sub": "john",
  "roles": ["ROLE_USER"]
}
```

---

## Configuration

This service is designed to retrieve configuration from a **Config Server** in cloud environments.

Typical externalized properties include:

* datasource configuration
* JWT secret
* token expiration
* refresh token expiration
* service registration settings
* server port

### Test Configuration

For tests, external dependencies such as Config Server should be disabled.

Example:

```yaml
spring:
  cloud:
    config:
      enabled: false
```

---

## Run Locally

### Prerequisites

* Java 17+
* Maven
* Running database
* Config Server running if required by your environment
* Service Discovery running if required by your environment

### Start the service

```bash
mvn clean spring-boot:run
```

### Run tests

```bash
mvn test
```

---

## Run with Docker

### Build the jar

```bash
mvn clean package
```

### Build the Docker image

```bash
docker build -t user-service:3.0 .
```

### Run the container

```bash
docker run -p 8083:8083 user-service:3.0
```

### Example with environment variables

```bash
docker run -p 8083:8083 \
  -e SPRING_PROFILES_ACTIVE=docker \
  -e JWT_SECRET=my-secret \
  user-service:3.0
```

If your architecture uses external services such as Config Server, Discovery, or Database, make sure the container can reach them through the configured network.

---

## Testing Strategy

This service includes several test layers to ensure reliability.

### Repository Tests

Verify:

* persistence behavior
* custom queries
* uniqueness checks for login and email

Typical annotation:

```java
@DataJpaTest
```

### Unit Tests

Verify:

* business rules
* password validation
* duplicate user checks
* JWT-related logic at service level

Typical tools:

* JUnit 5
* Mockito

### Slice Tests

Verify:

* controller mappings
* HTTP status codes
* JSON request/response handling
* validation behavior

Typical annotation:

```java
@WebMvcTest
```

### Integration Tests

Verify full application behavior across:

```text
Controller
 ↓
Service
 ↓
Repository
 ↓
Security
```

Typical annotation:

```java
@SpringBootTest
```

### Test Dependencies Added

* `spring-security-test`
* `jackson-databind`

---

## Observability

The service is designed to integrate with a cloud-native observability stack:

* **OpenTelemetry**
* **Prometheus**
* **Loki**
* **Tempo**
* **Grafana**

This supports:

* authentication tracing
* login latency measurement
* refresh/logout monitoring
* error tracking
* health visibility

---

## Version History

### v3

* Added login and refresh token workflow
* Added logout / token revocation support
* Updated JWT access token generation using `UserDetails`
* Replaced signup role type from `int` to `Role_Enum`
* Added Builder pattern to `Users`
* Added `LoginOrEmailExistException`
* Improved signup validation and error handling
* Replaced user initialization with builder-based creation
* Added repository tests
* Added unit tests
* Added controller slice tests
* Added integration tests
* Added `spring-security-test` and `jackson-databind`
* Added Docker support

### v2

* Added Principe of Clean Code

### v1

* Initial service foundation
* Basic JWT authentication
* Signup flow
* Initial role management

---

## Roadmap

* Add email verification flow
* Add password reset flow
* Add OAuth2 / social login
* Add OpenAPI / Swagger documentation
* Add CI/CD pipeline badges
* Add production deployment manifests

---

## Author

**Bah Youne**

Founder & Backend / Full Stack Java Developer

* GitHub: [http://github.com/bahyoune]
* LinkedIn: [http://linkedin.com/in/younoussa-bah]

---

## License

This project is shared for educational, portfolio, and demonstration purposes unless specified otherwise.



