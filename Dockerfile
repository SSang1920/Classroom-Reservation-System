# 1. JDK 17이 설치된 경량 리눅스 이미지 사용
FROM eclipse-temurin:17-jre-alpine

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. JAR 파일 경로를 받아서 컨테이너 내부에 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 4. 애플리케이션 포트
EXPOSE 8080

# 5. 애플리케이션 실행 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]