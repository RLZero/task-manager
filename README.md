# Task Management API
A Spring Boot REST API to manage tasks, supporting CRUD operations with PostgreSQL database.
[This api was build only for learning and practice purposed]

## Technologies
- Java 25
- Spring Boot 4
- PostgreSQL 17
- Maven
- Hibernate + JPA
- Mockito + Junit
- Testcontainers + RestTestClient (for integration tests)
- Swagger / OpenAPI
- Docker Compose

## Setup
- Clone the project : git clone https://github.com/RLZero/task-manager.git
- This project run using Docker Compose, which sets up PostgreSQL database automatically. So make sure you have [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/) installed. From the project root, run:
```bash
docker-compose up
```
- Build the project : `mvn clean install`
- Run the project : `mvn spring-boot:run`

## Access Swagger UI
- When your project is running locally you can access it with : http://localhost:8080/swagger-ui

