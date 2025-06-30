FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/baithak-0.0.1-SNAPSHOT.jar baithak-0.0.1-SNAPSHOT.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "baithak-0.0.1-SNAPSHOT.jar"]
