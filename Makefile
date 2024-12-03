HELM=helm
NAMESPACE=default

RELEASE_NAME=todo-app
CHART_PATH=./helm/release/todo-list
ENV ?= local
VALUES_FILE=$(CHART_PATH)/values-$(ENV).yaml

KAFKA_RELEASE=kafka
KEYCLOAK_RELEASE=keycloak
POSTGRES_RELEASE=postgresql
KAFKA_CHART=helm/kafka
KEYCLOAK_CHART=helm/keycloak
POSTGRES_CHART=helm/postgresql

all: dependencies postgres keycloak app

# Update Helm chart dependencies for all relevant charts
dependencies:
	@echo "Updating Helm chart dependencies for $(CHART_PATH)..."
	$(HELM) dependency update $(CHART_PATH)
	@echo "Updating Helm chart dependencies for $(KEYCLOAK_CHART)..."
	$(HELM) dependency update $(KEYCLOAK_CHART)
	@echo "Updating Helm chart dependencies for $(POSTGRES_CHART)..."
	$(HELM) dependency update $(POSTGRES_CHART)

# Install PostgreSQL
postgres:
	@echo "Deploying PostgreSQL..."
	$(HELM) upgrade --install $(POSTGRES_RELEASE) $(POSTGRES_CHART) --namespace $(NAMESPACE) -f $(POSTGRES_CHART)/values-$(ENV).yaml --wait

# Install Kafka
kafka:
	@echo "Deploying Kafka..."
	$(HELM) upgrade --install $(KAFKA_RELEASE) $(KAFKA_CHART) --namespace $(NAMESPACE) -f $(KAFKA_CHART)/values.yaml --wait

# Install Keycloak
keycloak:
	@echo "Deploying Keycloak..."
	$(HELM) upgrade --install $(KEYCLOAK_RELEASE) $(KEYCLOAK_CHART) --namespace $(NAMESPACE) -f $(KEYCLOAK_CHART)/values-$(ENV).yaml --wait

# Install or upgrade the main todo app
app:
	@echo "Deploying todo-app with values from $(VALUES_FILE)..."
	$(HELM) upgrade --install $(RELEASE_NAME) $(CHART_PATH) --namespace $(NAMESPACE) -f $(VALUES_FILE) --wait

# Uninstall all (Postgres, Keycloak, and the app)
uninstall:
	@echo "Uninstalling PostgreSQL..."
	$(HELM) uninstall $(POSTGRES_RELEASE) --namespace $(NAMESPACE)
	@echo "Uninstalling Keycloak..."
	$(HELM) uninstall $(KEYCLOAK_RELEASE) --namespace $(NAMESPACE)
	@echo "Uninstalling todo-app..."
	$(HELM) uninstall $(RELEASE_NAME) --namespace $(NAMESPACE)

# Package the todo-app chart
package:
	@echo "Packaging Helm chart for $(ENV) environment..."
	$(HELM) package $(CHART_PATH)

# Clean up resources (Postgres, Keycloak, and the app)
clean: uninstall
	@echo "Cleaning up persistent volumes..."
	kubectl delete pvc --all --namespace $(NAMESPACE)
	kubectl delete pv --all --namespace $(NAMESPACE)
