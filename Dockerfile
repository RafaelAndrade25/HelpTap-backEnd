# Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
# Resolving dependencies first to cache this layer
RUN mvn dependency:go-offline

COPY src ./src
# Package the application skipping tests to speed up the build in the cloud
RUN mvn clean package -DskipTests

# Run stage (Production)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the compiled jar from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose default spring boot port
EXPOSE 8080

# Run the application with explicit memory limits to prevent high costs on Railway
# -Xmx200m: Max Heap 200MB
# -Xms100m: Initial Heap 100MB
# -Xss512k: Thread Stack Size 512KB
ENTRYPOINT ["java", "-Xmx200m", "-Xms100m", "-Xss512k", "-jar", "app.jar"]
    