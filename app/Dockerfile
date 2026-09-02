FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build

# Install Maven package directly in Alpine builder
RUN apk add --no-cache maven

# Copy POM and pre-fetch dependencies
COPY app/pom.xml ./app/
RUN mvn dependency:go-offline -B -f ./app/pom.xml

# Copy application source code and package JAR
COPY app/src ./app/src
RUN mvn clean package -DskipTests -f ./app/pom.xml

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]