
# ToDoListApp - Production Setup

ToDoListApp is a task management application that allows users to organize their tasks efficiently. This guide provides instructions for setting up the application in a production environment hosted on Google Cloud Kubernetes. Once set up, the application can be accessed at [https://todolist.ooguy.com/](https://todolist.ooguy.com/).

## Prerequisites

### 1. Docker
Docker is required to build and push images to Docker Hub. Download and install Docker from the [official Docker website](https://docs.docker.com/get-docker/).

Verify Docker installation:

```bash
docker --version
```

### 2. Kubernetes Cluster (Google Cloud)
For production, a Google Cloud Kubernetes cluster is recommended. If you want to host on a cloud provider, follow these steps to create and connect to a Google Cloud cluster:

1. **Create a new project**, for example, `todo-app`.
2. **Prepare the cluster**:
   - Name: `todo-app-cluster`
   - Region: `us-central1`
   - Nodes:
     - Series: `E2 - e2-standard-2`
     - Disk size: `50GB`

3. **Connect to the cluster**:
   ```bash
   gcloud container clusters get-credentials todo-app-cluster --region us-central1 --project todo-app
   ```

### 3. Docker Hub (Image Pushing)
In production, images must be pushed to Docker Hub:

```bash
docker push docker.io/ravv1/configserver:v1
docker push docker.io/ravv1/edgeserver:v1
docker push docker.io/ravv1/useragent:v1
docker push docker.io/ravv1/taskmanager:v1
docker push docker.io/ravv1/ui:v1-prod
```

### 4. Kubernetes Ingress Controller
Install the NGINX ingress controller to manage incoming traffic:

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/cloud/deploy.yaml
```

### 5. Helm
Install Helm as it is required to manage and deploy applications within the Kubernetes cluster.

---

## Production Setup Instructions

Follow these steps to set up ToDoListApp in a production environment:

### 1. **Build and Push Docker Images**
Run the build script to compile projects and push Docker images to Docker Hub:

```bash
./build.sh
```

### 2. **Update Helm Chart Dependencies**
Update Helm dependencies to ensure all required charts are configured:

```bash
make dependencies
```

### 3. **Set Up PostgreSQL**
Initialize PostgreSQL for production by running:

```bash
make postgres ENV=prod
```

### 4. **Configure Keycloak**
To set up Keycloak, run the following command:

```bash
make keycloak ENV=prod --always-make
```

#### External Access for Keycloak Admin Console
For production, use an Ingress IP address to access Keycloak:

1. Retrieve the external IP for ingress:
   ```bash
   kubectl get svc -n ingress-nginx -l app.kubernetes.io/name=ingress-nginx -o jsonpath='{.items[0].status.loadBalancer.ingress[0].ip}'
   ```
   
2. Configure DNS to point the domain `https://todolistauth.giize.com/` to this IP.

Once configured, access the Keycloak Admin Console at:

- URL: [https://todolistauth.giize.com/](http://todolistauth.giize.com/)

To retrieve the external IP for the edge server, set up a DNS record to point `https://todolist.ooguy.com/` to the IP of the `edgeserver` service:
   ```bash
   kubectl get svc edgeserver
   ```

### 5. **Import Keycloak Realm Configuration**
For production, use the `realm-prod.json` file:

1. The production realm configuration is located in:
   ```
   keycloak/realm-prod.json
   ```

2. After Keycloak is running, log into the admin console and import the `realm-prod.json` file under **Realm Settings**.

### 6. **Update Keycloak Client Secret**
Follow these steps to configure the client secret in Kubernetes:

1. Access the Keycloak Admin Console at `https://todolistauth.giize.com/` and log in with admin credentials.
2. Go to **Clients** > `userAgentClient` > **Credentials** and regenerate the client secret.
3. Base64 encode the client secret:
   ```bash
   echo -n 'your_client_secret' | base64
   ```

4. Update the secret in `useragent-secret.yaml`:
   ```yaml
   data:
     keycloak_client_secret: <base64_encoded_secret>
   ```

5. Apply the updated secrets for production:
   ```bash
   kubectl apply -f kubernetes/environments/prod/secrets/useragent-secret.yaml
   kubectl apply -f kubernetes/environments/prod/secrets/taskmanager-secret.yaml
   kubectl apply -f kubernetes/environments/prod/secrets/edgeserver-secret.yaml
   kubectl apply -f kubernetes/environments/prod/secrets/fileprocesor-secret.yaml
   ```

### 7. **Deploy the Application**
Install the ToDoListApp by running:

```bash
make app ENV=prod
```

---

## Accessing ToDoListApp in Production

To access the production version of ToDoListApp, navigate to [http://todolist.ooguy.com/home](http://todolist.ooguy.com/home) (assuming DNS configuration points this domain to the `edgeserver` service’s external IP).
