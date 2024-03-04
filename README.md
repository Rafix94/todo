# ToDoListApp

ToDoListApp is a task management application that allows users to organize their tasks efficiently.

## Prerequisites

Before running ToDoListApp, ensure you have the following installed on your system:

- Docker
- Docker Compose
- Maven

## Getting Started

To get started with ToDoListApp, follow these steps:

1. **Build the Projects**: Navigate to the root directory of each project (`useragent`, `edgeserver`, `configserver`, `eureka`) and run the build script provided (`build.sh`). This script will compile the projects and build Docker images.

    ```bash
    ./build.sh
    ```

2. **Run Docker Compose**: After building the projects, run Docker Compose `dockercompose/local/docker-compose.yml` to start all the services.

    ```bash
    docker-compose up
    ```
3. **Explore ToDoListApp**: Access the ToDoListApp through the user-friendly graphical user interface (GUI) available at http://localhost:4200/home.

---

## App Details

### Overview
This application follows a microservices architecture and is managed using Docker Compose. It comprises various components that collaborate to deliver different functionalities.

### Components
1. Config Server: This server provides configurations for other microservices such as edgeserver, eureka, and useragent. Configuration files are stored in the todo-config repository https://github.com/Rafix94/todo-config.

2. Eureka Server: Located at http://localhost:8091/, the Eureka server maintains a registry of registered microservices. Both edgeserver and useragent register with Eureka, enabling traffic load balancing across multiple instances.

3. Edgeserver: Serving as a load balancer, this component implements the circuit breaker pattern for the useragent service. It logs and stores all incoming requests, including their paths, methods, bodies, and headers, in a PostgreSQL database managed by Liquibase.

4. Keycloak: Used for securing the application using a stateless method with JWT tokens, Keycloak facilitates Single Sign-On (SSO) and addresses security challenges inherent in microservices architecture.

5. Useragent: This microservice handles user requests and provides CRUD (Create, Read, Update, Delete) operations for tasks. It also manages user registration. The API is documented and accessible at http://localhost:8092/swagger-ui/index.html#. Similar to edgeserver, it utilizes a PostgreSQL database managed by Liquibase for storing data.

---
