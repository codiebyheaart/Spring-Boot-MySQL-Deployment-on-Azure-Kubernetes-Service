# ---------- Stage 1: Build (builder image) ----------
FROM gradle:8.8-jdk17 AS builder
 
WORKDIR /app
 
# Copy only necessary files for dependency download
COPY build.gradle ./
COPY src ./src
 
# Build application
RUN gradle clean build --no-daemon --stacktrace
 
 
# ---------- Stage 2: Run (runtime image) ----------
FROM eclipse-temurin:17-jre-alpine
 
WORKDIR /app
 
# Copy built artifact from builder stage
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar
 
# Expose container port (optional)
EXPOSE 8080
 
# Health check (optional but recommended)
HEALTHCHECK --interval=30s --timeout=5s \
  CMD wget -q http://localhost:8080/actuator/health || exit 1
 
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
