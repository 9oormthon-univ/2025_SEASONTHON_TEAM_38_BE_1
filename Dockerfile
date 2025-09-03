FROM openjdk:17-jdk-slim

WORKDIR /app

COPY build/libs/simhae-0.0.1-SNAPSHOT.jar /app/simhae.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/simhae.jar"]