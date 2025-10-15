# Use a lightweight JDK image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy all project files
COPY . .

# Package the application (skip tests to speed up)
RUN ./mvnw clean package -DskipTests

# Expose the Spring Boot default port
EXPOSE 8080

# Run the packaged JAR file
CMD ["java", "-jar", "target/ecommerce-app-1.0.0.jar"]
