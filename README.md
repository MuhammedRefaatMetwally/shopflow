# ShopFlow — E-commerce REST API

A production-grade REST API for e-commerce, built with Java and Spring Boot.

## Tech Stack

- **Java 17** + **Spring Boot 4**
- **Spring Security** — JWT authentication, role-based access control
- **Spring Data JPA** + **Hibernate** — ORM, entity relationships
- **PostgreSQL** — relational database
- **Docker** + **Docker Compose** — containerized deployment
- **Maven** — build tool
- **JUnit 5** + **Mockito** — unit testing

## Features

- JWT authentication (register, login)
- Role-based access: `ADMIN` and `CUSTOMER`
- Product management with soft delete and category filtering
- Order placement with stock validation and price snapshot
- Global exception handling with clean JSON error responses
- Swagger UI for interactive API documentation

## Running Locally

**Prerequisites:** Docker Desktop must be running.
```bash
git clone https://github.com/YOUR_USERNAME/ShopFlow.git
cd ShopFlow
docker-compose up --build
```

API: `http://localhost:8080`
Swagger UI: `http://localhost:8080/swagger-ui.html`

## API Endpoints

### Auth — public
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new customer |
| POST | `/api/auth/login` | Login and receive JWT |

### Products — GET is public, write requires ADMIN
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | Get all active products |
| GET | `/api/products/{id}` | Get product by ID |
| GET | `/api/products/search?name=` | Search by name |
| GET | `/api/products/category/{id}` | Filter by category |
| POST | `/api/products` | Create product (ADMIN) |
| PUT | `/api/products/{id}` | Update product (ADMIN) |
| DELETE | `/api/products/{id}` | Soft delete product (ADMIN) |

### Orders — requires authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Place order (CUSTOMER) |
| GET | `/api/orders/my-orders` | View my orders (CUSTOMER) |
| GET | `/api/orders/my-orders/{id}` | View order detail (CUSTOMER) |
| GET | `/api/orders` | View all orders (ADMIN) |
| PUT | `/api/orders/{id}/status` | Update order status (ADMIN) |

## Running Tests
```bash
./mvnw test
```

## Git Workflow

This project follows a feature branch workflow with conventional commits:
- `main` — stable, production-ready
- `develop` — integration branch
- `feature/*` — individual features, merged via Pull Requests

## Project Structure
```
src/main/java/com/shopflow/
├── config/          # Spring Security configuration
├── controller/      # REST controllers (HTTP layer)
├── service/         # Business logic
├── repository/      # JPA repositories (data access)
├── entity/          # JPA entities (database tables)
├── dto/             # Request and response objects
├── exception/       # Custom exceptions + global handler
├── security/        # JWT filter and UserDetails
└── enums/           # Role, OrderStatus
```