####
# Multi-stage Dockerfile for Swarm API (Quarkus application)
#
# This Dockerfile builds the application from source using Maven in a builder stage,
# then copies only the necessary runtime artifacts to a lightweight runtime image.
#
# Build arguments:
#   MAVEN_NEXUS_USERNAME: Username for Nexus repository authentication (default: jenkins)
#   MAVEN_NEXUS_PASSWORD: Password for Nexus repository authentication (required for build)
#
# Build the image:
#   docker build --build-arg MAVEN_NEXUS_PASSWORD=your_password -t swarm-api:latest .
#
# Run the container:
#   docker run -d -p 8080:8080 --env-file .env swarm-api:latest
#
# For development with debug enabled:
#   docker run -d -p 8080:8080 -p 5005:5005 \
#     -e JAVA_DEBUG=true -e JAVA_DEBUG_PORT=*:5005 \
#     --env-file .env swarm-api:latest
####

###
# Stage 1: Build stage using Maven
###
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

# Install required packages
RUN apk add --no-cache git

# Set working directory
WORKDIR /build

# Create .m2 directory and copy Maven settings
RUN mkdir -p /root/.m2
COPY settings.xml /root/.m2/settings.xml

# Copy Maven wrapper and pom.xml first to leverage Docker layer caching
COPY .mvn/ .mvn/
COPY mvnw mvnw.cmd pom.xml ./

# Fix line endings and make mvnw executable
RUN chmod +x mvnw && \
    dos2unix mvnw || sed -i 's/\r$//' mvnw || true

# Verify settings.xml is correct
RUN cat /root/.m2/settings.xml && echo "Settings file OK"

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B -s /root/.m2/settings.xml

# Copy source code and configuration
COPY src/ src/
COPY config/ config/

# Build the application (skip tests in Docker build for speed - run tests in CI/CD)
RUN mvn package -DskipTests -B -s /root/.m2/settings.xml && \
    # Verify the build output exists
    ls -lh target/quarkus-app/

###
# Stage 2: Runtime stage with optimized JVM image
###
FROM registry.access.redhat.com/ubi8/openjdk-21:1.19

# Metadata labels
LABEL maintainer="Edifice" \
      description="Swarm API - Quarkus microservice" \
      version="0.1.0" \
      java.version="21"

ENV LANGUAGE='en_US:en'

# Create deployment directory
WORKDIR /deployments

# Copy the built artifacts from builder stage
# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --from=builder --chown=185 /build/target/quarkus-app/lib/ /deployments/lib/
COPY --from=builder --chown=185 /build/target/quarkus-app/*.jar /deployments/
COPY --from=builder --chown=185 /build/target/quarkus-app/app/ /deployments/app/
COPY --from=builder --chown=185 /build/target/quarkus-app/quarkus/ /deployments/quarkus/

# Expose application port
EXPOSE 8080

# Optional: Expose debug port (uncomment if needed)
# EXPOSE 5005

# Switch to non-root user for security
USER 185

# Configure JVM options for Quarkus
ENV JAVA_OPTS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

# Health check configuration (optional but recommended)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/q/health/ready || exit 1

# Run the application using the run-java.sh script
# This script provides automatic memory tuning and GC configuration
ENTRYPOINT [ "/opt/jboss/container/java/run/run-java.sh" ]
