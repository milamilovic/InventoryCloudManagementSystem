# Use an official Java runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file from the host into the container at /app
COPY target/payment-service-0.0.1-SNAPSHOT.jar /app/payment-service.jar

#Copy the database files
COPY src/main/resources/db/changelog/db.changelog-master.yaml /app/db/changelog/db.changelog-master.yaml
COPY src/main/resources/db/changelog/changelog-1.yaml /app/db/changelog/changelog-1.yaml

# Expose the port the app runs on
EXPOSE 8083

# Run the jar file
ENTRYPOINT ["java", "-jar", "/app/payment-service.jar"]
