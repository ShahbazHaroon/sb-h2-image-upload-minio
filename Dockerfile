# ---- Build Stage ----
FROM maven:3.9.9-eclipse-temurin-17 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy only POM files first to leverage Docker cache
COPY pom.xml ./

# Use Docker build cache for Maven dependencies
# This ensures that ~/.m2 is cached between builds
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B

# Copy full source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Get the final JAR name from Maven
RUN echo "Final JAR:" && ls -l target

# ---- Runtime Stage ----
FROM eclipse-temurin:17-jre-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy only the built JAR (avoid copying everything) into the container at /app
COPY --from=builder /app/target/*.jar sb-h2-image-upload-minio.jar

# Run as non-root user
# Create non-root user (use fixed UID/GID for reproducibility)
RUN groupadd -r spring && useradd -r -g spring spring

# Create log directory and set ownership/permissions before switching user
RUN mkdir -p /app/logs/sb-h2-image-upload-minio && \
    chown -R spring:spring /app/logs && \
    chmod -R 755 /app/logs

# Switch to non-root user
USER spring

# Expose the port your application will run on
EXPOSE 8080

# Use ENTRYPOINT for immutable command and CMD for arguments
ENTRYPOINT ["java", "-jar", "/app/sb-h2-image-upload-minio.jar"]
CMD []
