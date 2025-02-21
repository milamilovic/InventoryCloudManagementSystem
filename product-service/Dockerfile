# Use an official Java runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from the host into the container at /app
COPY target/product-service-0.0.1-SNAPSHOT.jar /app/product-service.jar

# Expose the port the app runs on
EXPOSE 8084

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app/product-service.jar"]
