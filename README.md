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
