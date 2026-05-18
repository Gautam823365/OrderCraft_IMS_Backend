# Stage 1: Build
FROM eclipse-temurin:17-jdk-focal AS builder

WORKDIR /app

# Copy Maven wrapper and pom
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy source
COPY src src

# Give permission
RUN chmod +x mvnw

# Build jar
RUN ./mvnw clean package -DskipTests

# Stage 2: Run app
FROM eclipse-temurin:17-jre-focal

WORKDIR /app

# Copy generated jar
COPY --from=builder /app/target/*.jar app.jar

# Render provides PORT automatically
EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
