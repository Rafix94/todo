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
docker build . -t ravv1/ui:v1