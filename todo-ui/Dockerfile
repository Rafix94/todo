FROM node:alpine

WORKDIR /usr/src/app

COPY . .

RUN npm install -g @angular/cli
RUN npm install

# Set a default profile
ENV NODE_ENV=${ANGULAR_ENV}

CMD ["sh", "-c", "NODE_OPTIONS='--max-old-space-size=8192' ng serve --configuration=${NODE_ENV} --host 0.0.0.0 --disable-host-check"]
