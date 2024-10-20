#!/bin/bash

# Build useragent
cd useragent || exit
mvn compile jib:dockerBuild
cd ..

# Build edgeserver
cd edgeserver || exit
mvn compile jib:dockerBuild
cd ..

# Build configserver
cd configserver || exit
mvn compile jib:dockerBuild
cd ..

# Build eureka
cd eureka || exit
mvn compile jib:dockerBuild
cd ..

# Build todo-ui
cd todo-ui || exit
docker buildx build --platform linux/amd64 --build-arg ANGULAR_ENV=local -t ravv1/ui:v1-local .
docker buildx build --platform linux/amd64 --build-arg ANGULAR_ENV=prod -t ravv1/ui:v1-prod .