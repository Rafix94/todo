
# ToDoListApp

ToDoListApp is a task management application that allows users to organize their tasks efficiently. The application is hosted on a Google Cloud cluster and can be accessed at [https://todolist.ooguy.com/](https://todolist.ooguy.com/).

Please note that both the frontend and Keycloak use self-signed certificates. You may need to allow insecure connections in your browser settings, as these connections might be treated as insecure.

# Setup Instructions

## Prerequisites

### 1. Docker
Docker is required to run containers for services such as PostgreSQL, Keycloak, and others. You can download and install Docker from the [official Docker website](https://docs.docker.com/get-docker/).

After installing Docker, verify that Docker is running and accessible by checking its version:

```bash
docker --version
```

### 2. Kubernetes
Kubernetes is used to orchestrate the different services in ToDoListApp. You can use [Docker Desktop](https://www.docker.com/products/docker-desktop/) to set up a local Kubernetes cluster easily.

To enable Kubernetes in Docker Desktop:
- Open Docker Desktop.
- Navigate to **Settings** -> **Kubernetes**.
- Check the option **Enable Kubernetes** and wait for the cluster to be configured.

Verify your Kubernetes installation by checking the version:

```bash
kubectl version --client
```

Ensure the cluster is running:

```bash
kubectl get nodes
```

### 3. Helm
Helm is a package manager for Kubernetes. You need Helm to install and manage the different services used in ToDoListApp, such as Keycloak.

Install Helm by following the [Helm installation guide](https://helm.sh/docs/intro/install/).

Verify the Helm installation:

```bash
helm version
```

---

## Getting Started

To get started with ToDoListApp, follow these steps:

### 1. **Build the Projects**:
Run the build script provided (`build.sh`). This script will compile the projects and build Docker images.

```bash
./build.sh
```

### 2. **Update Helm Chart Dependencies**:
Before deploying services, you should update the Helm chart dependencies for the application. This ensures that all required charts are properly configured.

```bash
make dependencies
```

This command updates dependencies for the ToDoListApp chart, Keycloak chart, and PostgreSQL chart.

### 3. **Set Up PostgreSQL**:
After building the projects and updating dependencies, set up the PostgreSQL database. This step is essential to initialize the databases used by the services. You can set up PostgreSQL using the following command:

```bash
make postgres
```
This command will configure the necessary databases and users for the project.

### 4. **Configure Keycloak**:
To set up Keycloak, run the following command:

```bash
make keycloak --always-make
```
After starting Keycloak, you will need to forward the Keycloak service to your local machine to access the admin console. Run the following command to forward the traffic:

- URL: [http://localhost](http://localhost/)

#### Retrieve Keycloak Admin Credentials
The credentials to log in to the Keycloak Admin Console are stored in Kubernetes secrets.
- You can retrieve the username by running the following command:
```bash
kubectl get configmap keycloak-env-vars -o jsonpath='{.data.KEYCLOAK_ADMIN}'
```
- You can retrieve the password by running the following command:
```bash
kubectl get secret keycloak -o jsonpath='{.data.admin-password}' | base64 --decode
```

### 5. **Import Keycloak Realm Configuration**:
You will need to import the Keycloak realm configuration to set up the correct authentication and authorization settings for the application.

1. The `realm-local.json` file is stored in the project at `keycloak/realm/realm.json`. You can find it in the following location within the repository:

```
keycloak/realm-local.json
```

2. Once Keycloak is running and you have logged in with the admin credentials, go to the Keycloak Admin Console, navigate to **Realm Settings**, and import the `realm-local.json` file.

### 6. **Update Keycloak Client Secret**:
After setting up Keycloak and creating the clients for both the User Agent (`userAgentClient`) and the Task Manager (`taskManagerClient`), follow these steps to update the Kubernetes secrets.

1. **Open Keycloak Admin Console**: Navigate to the Keycloak Admin Console at `http://localhost:80` and log in using your admin credentials.

2. **Select the User Agent Client**:
   - **User Agent Client**:
      - In the left sidebar, go to **Clients** and select `userAgentClient`.
      - Go to the **Credentials** tab and regenerate the client secret.
      - Copy the generated client secret.
   - **Task Manager Client**
      - Similarly, go to **Clients** and select `taskManagerClient`.
      - Go to the **Credentials** tab, regenerate the client secret, and copy it.
3. **Base64 Encode Each Client Secret**:

   Use the following commands in your terminal to base64 encode each client secret:
   ```bash
   echo -n 'your_userAgent_client_secret' | base64
   echo -n 'your_taskManager_client_secret' | base64
   ```

4. **Update Secret Value**:
   Update the encoded client secrets in the corresponding YAML files:
   - **For `userAgentClient`**: Open `useragent-secret.yaml` and update `data.keycloak_client_secret` with the encoded secret.
   - **For `taskManagerClient`**: Open `taskmanager-secret.yaml` and update `data.keycloak_client_secret` with the encoded secret.

7. **Apply the Changes to Your Kubernetes Cluster:**

   After updating the YAML files, apply the changes with the following commands:
   ```bash
   kubectl apply -f kubernetes/environments/local/secrets/useragent-secret.yaml
   kubectl apply -f kubernetes/environments/local/secrets/taskmanager-secret.yaml
   kubectl apply -f kubernetes/environments/local/secrets/edgeserver-secret.yaml
   kubectl apply -f kubernetes/environments/local/secrets/fileprocesor-secret.yaml
   ```

### 7. **Install the Application**:
Install the ToDoListApp with the following command:

```bash
make app
```

### 8. **Explore ToDoListApp**:
Access the ToDoListApp through the graphical user interface (GUI) available at [http://localhost:4200/home](http://localhost:4200/home).

---

## App Details

### Overview
This application follows a microservices architecture and is managed using Docker Compose. It comprises various components that collaborate to deliver different functionalities.

from pathlib import Path

### Components

1. **Config Server**: This server provides configurations for other microservices, such as EdgeServer, Eureka, and UserAgent. Configuration files are stored in the `todo-config` repository [here](https://github.com/Rafix94/todo-config).

2. **EdgeServer**: Acting as a load balancer, this component implements the circuit breaker pattern specifically for the UserAgent service. It logs and stores all incoming requests, including their paths, methods, bodies, and headers, in a PostgreSQL database managed by Liquibase.

3. **Keycloak**: Responsible for securing the application via stateless authentication with JWT tokens, Keycloak enables Single Sign-On (SSO) and manages both user and team assignments. This centralizes identity and access management, addressing key security challenges in a microservices architecture.

4. **UserAgent**: This microservice manages users and teams, with teams also being handled by Keycloak.

5. **Task Manager**: This microservice provides CRUD (Create, Read, Update, Delete) operations for tasks.