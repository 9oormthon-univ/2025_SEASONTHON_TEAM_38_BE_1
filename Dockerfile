FROM eclipse-temurin:17-jdk
WORKDIR /app

# Spring Boot jar 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /app.jar

# Nginx 설치
RUN apt-get update && apt-get install -y nginx supervisor && \
    rm -rf /var/lib/apt/lists/*

# 설정 파일 복사
COPY nginx.conf /etc/nginx/nginx.conf
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf

EXPOSE 80 8080

# supervisord -> Spring Boot + Nginx 실행
CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]