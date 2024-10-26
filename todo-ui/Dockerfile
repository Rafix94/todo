# Stage 1: Build the Angular app
FROM node:16 AS build

# Set working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json for dependency installation
COPY package*.json ./

# Install Angular dependencies
RUN npm install

# Copy the Angular app source code
COPY . .

# Build the Angular app based on the environment
ARG env=local
RUN npm run build -- --configuration=$env

# Stage 2: Set up NGINX to serve the app
FROM nginx:alpine

# Copy the build output to NGINX's default public directory
COPY --from=build /app/dist /usr/share/nginx/html

# Copy the custom NGINX configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

# Start NGINX server
CMD ["nginx", "-g", "daemon off;"]
