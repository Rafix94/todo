FROM node:alpine

WORKDIR /usr/src/app

COPY . .

RUN npm install -g @angular/cli
RUN npm install

# Set a default profile
ENV NODE_ENV=${SPRING_PROFILES_ACTIVE}

CMD ["sh", "-c", "ng serve --configuration=local-kubernetes --host 0.0.0.0"]
