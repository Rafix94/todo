#!/bin/bash

# Build useragent
cd useragent || exit
mvn clean compile jib:dockerBuild
cd ..

# Build taskmanager
cd taskmanager || exit
mvn clean compile jib:dockerBuild
cd ..

# Build edgeserver
cd edgeserver || exit
mvn clean compile jib:dockerBuild
cd ..

# Build configserver
cd configserver || exit
mvn clean compile jib:dockerBuild
cd ..

# Build todo-ui
cd todo-ui || exit
docker buildx build --platform linux/amd64 --build-arg env=local -t ravv1/ui:v1-local .
docker buildx build --platform linux/amd64 --build-arg env=prod -t ravv1/ui:v5-prod .