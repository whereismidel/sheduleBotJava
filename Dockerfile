#=============================
# Build container
#=============================
FROM maven AS builder

# Set working directory to /src
WORKDIR /build

# Copy pom and root src
COPY pom.xml /build/pom.xml
RUN mvn -e -B dependency:go-offline

COPY src /build/src
RUN mvn -e -B clean install package -DskipTests && mv -v /build/target/*.jar /build/target/app.jar

#==============================
# Final container
#==============================
FROM openjdk as runner

# Set the working directory
WORKDIR /app

# Copy jar file
COPY --from=builder /build/target/app.jar /app/

WORKDIR /app/workdir

# Run the jar file
CMD ["java", "-jar", "/app/app.jar"]
