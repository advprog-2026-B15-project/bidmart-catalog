# Build stage for Next.js Frontend
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# Build stage for Spring Boot Backend
FROM gradle:8-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Copy built frontend assets to Spring Boot static resources
COPY --from=frontend-build /app/frontend/out /home/gradle/src/src/main/resources/static
RUN gradle build --no-daemon -x test

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
RUN mkdir uploads
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
