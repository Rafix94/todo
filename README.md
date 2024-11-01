
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
After setting up Keycloak and creating the client for the User Agent, you will need to copy the generated client secret and update your Kubernetes secrets.

1. **Open Keycloak Admin Console**: Navigate to the Keycloak Admin Console at [http://localhost:80](http://localhost:80) and log in with your admin credentials.

2. **Select the User Agent Client**: In the left sidebar, go to **Clients** and select the `userAgentClient`.

3. **Regenerate the Client Secret**: Go to the **Credentials** tab and regenerate the client secret.

4. **Copy the Client Secret**: Copy the generated client secret.

5. **Base64 Encode the Client Secret**: Use the following command in your terminal to base64 encode the client secret:

    ```bash
    echo -n 'your_client_secret' | base64
    ```

6. **Update Secret Value**: Take the output from the previous command and update the secret value in `useragent-secret.yaml` under `data.keycloak_client_secret`.

7. **Apply the Changes**: After updating the `useragent-secret.yaml` file, apply the changes to your Kubernetes cluster:

    ```bash
    kubectl apply -f kubernetes/environments/local/secrets/useragent-secret.yaml
    kubectl apply -f kubernetes/environments/local/secrets/edgeserver-secret.yaml
    kubectl apply -f kubernetes/environments/local/secrets/taskmanager-secret.yaml
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

### Components
1. **Config Server**: This server provides configurations for other microservices such as edgeserver, eureka, and useragent. Configuration files are stored in the todo-config repository [https://github.com/Rafix94/todo-config](https://github.com/Rafix94/todo-config).

2. **Eureka Server**: Located at [http://localhost:8091/](http://localhost:8091/), the Eureka server maintains a registry of registered microservices. Both edgeserver and useragent register with Eureka, enabling traffic load balancing across multiple instances.

3. **Edgeserver**: Serving as a load balancer, this component implements the circuit breaker pattern for the useragent service. It logs and stores all incoming requests, including their paths, methods, bodies, and headers, in a PostgreSQL database managed by Liquibase.

4. **Keycloak**: Used for securing the application using a stateless method with JWT tokens, Keycloak facilitates Single Sign-On (SSO) and addresses security challenges inherent in microservices architecture.

5. **Useragent**: This microservice handles user requests and provides CRUD (Create, Read, Update, Delete) operations for tasks. It also manages user registration. The API is documented and accessible at [http://localhost:8092/swagger-ui/index.html#](http://localhost:8092/swagger-ui/index.html#). Similar to edgeserver, it utilizes a PostgreSQL database managed by Liquibase for storing data.

--- 
