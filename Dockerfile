# Step 1: Use OpenJDK image
FROM openjdk:17-jdk-slim

# Step 2: Set working directory
WORKDIR /app

# Step 3: Copy Maven files and build app
COPY . .

# Step 4: Build using Maven Wrapper
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Step 5: Run the app
CMD ["java", "-jar", "target/ecommerce-app-0.0.1-SNAPSHOT.jar"]
