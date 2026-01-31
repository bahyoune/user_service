# User Service (Authentication & Authorization)

The **User Service** is responsible for **user identity management and authentication**.

It provides:

* User and role management
* Secure credential storage
* JWT access token generation
* Refresh token support
* Authentication endpoints used by the API Gateway

This service acts as the **identity provider** of the platform.

---

## Responsibilities

The User Service handles:

* User registration
* Role creation and assignment
* Authentication (login)
* JWT access token generation
* Refresh token issuance & rotation
* Credential security

All authentication logic is centralized here.

---

## Security Model

### Overview

The platform uses a **JWT-based authentication model**:

* **Access Token**

  * Short-lived
  * Sent with every API request
* **Refresh Token**

  * Long-lived
  * Used to obtain new access tokens
  * Never sent to business services

The API Gateway validates access tokens before routing requests.

---

### Authentication Flow

```
Client
  ↓ (credentials)
User Service
  ↓ (JWT + Refresh Token)
Client
  ↓ (JWT)
API Gateway
  ↓
Internal Services
```

---

## User & Role Model

### User

* Username / email
* Encrypted password
* One or more roles
* Account status (enabled, locked, etc.)

### Role

* Logical authority (e.g. USER, ADMIN)
* Used for authorization at the gateway or service level

---

## Token Types

### Access Token (JWT)

* Short-lived (e.g. 15 minutes)
* Contains:

  * User identifier
  * Roles / authorities
  * Expiration timestamp
* Sent in `Authorization` header

### Refresh Token

* Long-lived (e.g. 7 days)
* Stored securely
* Used only with the User Service
* Supports token rotation

---

## API Endpoints

### Register User

```http
POST /auth/register
```

**Request**

```json
{
  "username": "john.doe",
  "password": "secret",
  "roles": ["USER"]
}
```

**Response**

```json
{
  "id": "uuid",
  "username": "john.doe",
  "roles": ["USER"]
}
```

---

### Login (Generate Tokens)

```http
POST /auth/login
```

**Request**

```json
{
  "username": "john.doe",
  "password": "secret"
}
```

**Response**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "d2b3c9e1-...",
  "expiresIn": 900
}
```

---

### Refresh Access Token

```http
POST /auth/refresh
```

**Request**

```json
{
  "refreshToken": "d2b3c9e1-..."
}
```

**Response**

```json
{
  "accessToken": "new.jwt.token",
  "expiresIn": 900
}
```

---

## Password & Token Security

* Passwords are stored using **BCrypt**
* Refresh tokens are:

  * Stored securely
  * Rotated on use
  * Invalidated on logout
* JWTs are:

  * Signed using a secure secret or key pair
  * Validated by the API Gateway

---

## Role-Based Access Control (RBAC)

Roles are embedded in the JWT:

```json
{
  "sub": "john.doe",
  "roles": ["USER"]
}
```

The API Gateway enforces:

* Route-level access
* Role-based authorization

Example:

```yaml
- Path=/admin/**
  Required role: ADMIN
```

---

## Service Isolation

The User Service:

* Is **not publicly exposed**
* Is only accessible via:

  * API Gateway
  * Internal network
* Never accessed directly by browsers

---

## Observability

The service integrates with:

* **OpenTelemetry**
* **Prometheus** (metrics)
* **Loki** (Logs)
* **Tempo** (distributed tracing)
* **Grafana** (dashboards)
* **Kafka metrics & traces**

This enables:

* Authentication latency monitoring
* Login failure tracking
* Token issuance metrics
* Distributed tracing

---

## Startup Order

The User Service depends on:

1. Config Server
2. Eureka Discovery
3. Database (User & Token storage)

Once started, it registers with Eureka and becomes available to the API Gateway.

---

## Why this design?

This design:

* Centralizes authentication
* Simplifies security management
* Scales independently
* Aligns with OAuth2/JWT best practices
* Works well in microservice architectures

It mirrors real-world identity services used in production systems.

---


## Summary

* Centralized user management
* Secure JWT authentication
* Refresh token support
* Role-based access control
* Gateway-integrated security
* Scalable & observable




