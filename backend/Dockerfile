# Stage 1: Build the application
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy Maven wrapper and project descriptor
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml ./

# Ensure the mvnw script has execution permissions and download dependencies
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code and package the application
COPY src src
RUN ./mvnw clean package -DskipTests

# Stage 2: Lightweight runtime environment
FROM eclipse-temurin:25-jre
WORKDIR /app

# Copy the compiled jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose default port
EXPOSE 8080

# Launch the application
ENTRYPOINT ["java", "-jar", "app.jar"]
