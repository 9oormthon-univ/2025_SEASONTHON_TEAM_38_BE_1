# Gradle 빌드 단계
FROM openjdk:17-jdk-slim AS build

WORKDIR /app

# 소스 복사
COPY . .

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# 빌드 실행 (테스트 제외 가능)
RUN ./gradlew clean build -x test --no-daemon

# 실행 이미지 단계
FROM openjdk:17-jdk-slim

WORKDIR /app

# 빌드된 JAR 복사
COPY --from=build /app/build/libs/simhae-0.0.1-SNAPSHOT.jar /app/simhae.jar

EXPOSE 8080

CMD ["java", "-Duser.timezone=Asia/Seoul", "-jar", "/app/simhae.jar"]