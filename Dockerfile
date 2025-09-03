FROM openjdk:17-jdk-slim AS build

WORKDIR /app

COPY . .
RUN ./gradlew clean build --no-daemon

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/build/libs/simhae-0.0.1-SNAPSHOT.jar /app/simhae.jar

CMD ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app/simhae.jar"]