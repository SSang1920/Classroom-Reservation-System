# 1. JDK 17이 설치된 경량 리눅스 이미지 사용
FROM openjdk:17-jdk-alpine

# 2. JAR 파일 경로를 받아서 컨테이너 내부에 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080

# 3. 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]