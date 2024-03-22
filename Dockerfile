# Stage 1: Build phase using Gradle
FROM gradle:8.6.0-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build

# Stage 2: Runtime image
FROM openjdk:21
RUN mkdir -p app
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar
VOLUME /workspace

EXPOSE 8080
ENTRYPOINT ["java","-jar","-Dserver.port=8080","-Dspring.profiles.active=prod","/app/app.jar"]
