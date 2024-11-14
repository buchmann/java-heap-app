# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the application jar
COPY target/java-heap-app-1.0-SNAPSHOT.jar /app/app.jar

# Copy the application.properties file
COPY src/main/resources/application.properties /app/application.properties

# Expose port 8080 for the web server
EXPOSE 8080

# Run the application with specific JVM options for heap size
CMD ["java", "-Xms512m", "-Xmx1g", "-Dcom.sun.management.jmxremote", "-Dcom.sun.management.jmxremote.port=9010", "-Dcom.sun.management.jmxremote.local.only=false", "-Dcom.sun.management.jmxremote.authenticate=false", "-Dcom.sun.management.jmxremote.ssl=false", "-jar", "app.jar", "--spring.config.location=file:/app/application.properties"]
