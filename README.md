# ToDoListApp

ToDoListApp is a task management application that allows users to organize their tasks efficiently.

## Prerequisites

Before running ToDoListApp, ensure you have the following installed on your system:

- Docker
- Docker Compose
- Maven

## Getting Started

To get started with ToDoListApp, follow these steps:

1. **Build the Projects**: Navigate to the root directory of each project (`useragent`, `edgeserver`, `configserver`, `eureka`) and run the build script provided (`build_all.sh`). This script will compile the projects and build Docker images.

    ```bash
    ./build_all.sh
    ```

2. **Run Docker Compose**: After building the projects, run Docker Compose to start all the services.

    ```bash
    docker-compose up
    ```

3. **Access Keycloak Admin Console**: Once the services are up and running, access the Keycloak Admin Console at [http://localhost:8080/](http://localhost:8080/) using the following credentials:
    - Username: admin
    - Password: admin

4. **Create Realm and Import Configuration**: In the Keycloak Admin Console, go to **Realms** and create a new realm. Then import the realm configuration file located at `dockercompose/local/docker-compose.yml`.

5. **Explore ToDoListApp**: Access the ToDoListApp through the user-friendly graphical user interface (GUI) available at http://localhost:4200/home.

Sure, here's the text without formatting:

---

## App Details

### Overview
This application follows a microservices architecture and is managed using Docker Compose. It comprises various components that collaborate to deliver different functionalities.

### Components
1. Config Server: This server provides configurations for other microservices such as edgeserver, eureka, and useragent. Configuration files are stored in the todo-config repository.

2. Eureka Server: Located at http://localhost:8091/, the Eureka server maintains a registry of registered microservices. Both edgeserver and useragent register with Eureka, enabling traffic load balancing across multiple instances.

3. Edgeserver: Serving as a load balancer, this component implements the circuit breaker pattern for the useragent service. It logs and stores all incoming requests, including their paths, methods, bodies, and headers, in a PostgreSQL database managed by Liquibase.

4. Keycloak: Used for securing the application using a stateless method with JWT tokens, Keycloak facilitates Single Sign-On (SSO) and addresses security challenges inherent in microservices architecture.

5. Useragent: This microservice handles user requests and provides CRUD (Create, Read, Update, Delete) operations for tasks. It also manages user registration. The API is documented and accessible at http://localhost:8092/swagger-ui/index.html#. Similar to edgeserver, it utilizes a PostgreSQL database managed by Liquibase for storing data.

---
