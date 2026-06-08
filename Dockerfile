# Stage 1: Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Copy gradle files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Grant execution permissions to gradlew
RUN chmod +x gradlew

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon

# Copy source code and config for checkstyle
COPY src src
COPY config config
RUN ./gradlew build -x test --no-daemon

# Stage 2: Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Only copy the final jar from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Create uploads directory
RUN mkdir -p uploads

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
