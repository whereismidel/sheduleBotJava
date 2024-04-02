FROM maven:3.8.4-openjdk-8 AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project file
COPY pom.xml .

# Download all required dependencies into one layer
RUN mvn dependency:go-offline -B

# Copy the source code of the project
COPY src ./src

# Building an application
RUN mvn package -DskipTests

# Use the AdoptOpenJDK image as the base image for the final stage
FROM adoptopenjdk:8-jre-hotspot

# Setup environment variables
ENV BOT_NAME=""
ENV BOT_TOKEN=""
ENV GOOGLE_CREDENTIALS=""

# Setup the working directory in the container
WORKDIR /app

# Copy the compiled application file from the previous stage
COPY --from=build /app/target/ScheduleBot-1.0.jar ./app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]