# Multi-stage build for RSLFrancoBot
# Stage 1: Build the Java application
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime image with Java + Python
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Install dependencies: python3, pip, git, build tools
RUN apk add --no-cache \
    python3 \
    py3-pip \
    git \
    build-base \
    curl

RUN pip3 install requests --break-system-packages

# Clone plando-random-settings at specific commit
RUN git clone https://github.com/matthewkirby/plando-random-settings.git ./plando-random-settings

# Checkout specific commit
RUN git -C plando-random-settings fetch && \
    git -C plando-random-settings checkout 50813f8

# Copy custom weight files
COPY weights/ ./plando-random-settings/weights/

# Copy data directory (preset definitions)
COPY src/main/resources/data/ ./data/

# Copy built JAR from builder stage
COPY --from=builder /app/target/*.jar ./app.jar

# Create non-root user for security
RUN addgroup -g 1000 botuser && \
    adduser -D -u 1000 -G botuser botuser && \
    chown -R botuser:botuser /app

USER botuser

# Expose health check port (if needed)
EXPOSE 8080

# Set environment variables (override via docker-compose or -e flags)
ENV DISCORD_TOKEN="" \
    RANDOMIZER_API_KEY="" \
    JAVA_OPTS="-Xmx512m -Xms256m" \
    SPRING_PROFILES_ACTIVE="prod"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=$SPRING_PROFILES_ACTIVE"]
