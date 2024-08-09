## Use a base image containing Java runtime
#FROM openjdk:21
#
## Set the working directory inside the container
#WORKDIR /app
#
## Copy the compiled JavaFX application into the container
#COPY /target/"demo3-1.0-SNAPSHOT.jar" app.jar
#
## Command to run the JavaFX application
#CMD ["java", "-jar", "app.jar"]
#

# Use an official OpenJDK runtime as a parent image
FROM openjdk:21

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY /target/demo3-1.0-SNAPSHOT.jar /app/JavaFXApp.jar

# Run the application
CMD ["java", "-jar", "/app/JavaFXApp.jar"]

## Copy the JAR file into the container at /app
#COPY YourJavaFXApp.jar /app/YourJavaFXApp.jar
#
## Run the application
#CMD ["java", "-jar", "/app/YourJavaFXApp.jar"]