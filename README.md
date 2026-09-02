Markdown
# Spring Boot 3.x + Oracle 23c PL/SQL Integration Architecture

A robust, end-to-end reference architecture demonstrating enterprise integration between Spring Boot 3.3.2 (Java 21) and Oracle Database 23c Free. 

Key Architectural Highlights:
• Multi-Module Maven Structure: Clean isolation of application code and database migration resources.
• Database Schema Orchestration: Liquibase-managed database changelogs supporting .pks and .pkb PL/SQL package deployments.
• Efficient Data Access: Spring JDBC SimpleJdbcCall integration optimized for PL/SQL procedures returning SYS_REFCURSOR dataset outputs.
• Resilient Error Handling: Custom Spring Exception Translation mapping domain-specific PL/SQL errors (ORA-20001).
• Automated Integration Testing: Isolated end-to-end test suites powered by Testcontainers running real Oracle 23c instances.
• Containerized Deployment: Multi-stage Docker builds orchestrated via Docker Compose with health-check dependency management.

---

## Technical Stack

- **Application**: Java 21, Spring Boot 3.3.2, Spring JDBC (`SimpleJdbcCall`), Jakarta Validation
- **Database**: Oracle Database 23c Free (`gvenzl/oracle-free:23-slim-faststart`)
- **Schema Management**: Liquibase (SQL change-sets & repeatable PL/SQL packages)
- **Testing**: Testcontainers (Oracle 23c container) & MockMvc

---

## Prerequisites

- **Docker Desktop** / **Rancher Desktop** (running Docker daemon)
- **Java 21 JDK** (for local application compilation/testing)
- **Maven 3.9+** (or use embedded `./mvnw`)

---

## Execution Instructions

### 1. Spin up Environment via Docker Compose

Build and launch both Oracle DB 23c and the Spring Boot application container with healthcheck synchronization:

```bash
docker compose up --build
The application will be accessible at http://localhost:8080.

2. Run Tests
Execute unit tests and Testcontainers integration tests locally:

Bash
# Run all unit and integration tests
./mvnw clean test

# Run Integration Tests only (Oracle 23c Testcontainer)
./mvnw test -Dtest=OrderPlsqlRepositoryIT

# Run MVC Controller Unit Tests only
./mvnw test -Dtest=OrderControllerTest
Local Development
If you prefer running the Oracle container independently and executing the Spring Boot app from your IDE or CLI:

Start only the Database:

Bash
docker compose up oracle-db -d
Run the Spring Boot application:

Bash
mvn spring-boot:run -pl app
Database Details
Image: gvenzl/oracle-free:23-slim-faststart

Port: 1521

Pluggable Database (PDB): FREEPDB1

Credentials: Specified in docker-compose.yml (SPRING_DATASOURCE_USERNAME / SPRING_DATASOURCE_PASSWORD)
