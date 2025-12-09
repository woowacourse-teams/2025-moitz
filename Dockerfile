# ========================================
# 빌드 스테이지: JAR 파일 생성
# ========================================
FROM --platform=linux/arm64 amazoncorretto:21-alpine AS builder

WORKDIR /workspace

# Gradle 파일 먼저 복사 (의존성 캐싱)
COPY backend/gradlew .
COPY backend/gradle gradle
COPY backend/build.gradle .
COPY backend/settings.gradle .

# Windows 줄바꿈 제거 및 실행 권한 부여
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# 의존성 다운로드 (캐시 레이어 생성)
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사
COPY backend/src src

# JAR 빌드
RUN ./gradlew clean build -x test --no-daemon

# JAR 파일명 확인 및 통일 (plain JAR 제외하고 실행 가능한 JAR만 선택)
RUN mv $(ls build/libs/*.jar | grep -v plain) build/libs/app.jar

# ========================================
# 런타임 스테이지: 실행 환경
# ========================================
FROM --platform=linux/arm64 amazoncorretto:21-alpine

WORKDIR /app

# 필요한 패키지 설치
RUN apk add --no-cache bash curl tzdata

# 타임존 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 빌드된 JAR 복사
COPY --from=builder /workspace/build/libs/app.jar /app/app.jar

# 로그 디렉토리 생성
RUN mkdir -p /app-logs/info /app-logs/error /app-logs/warn

EXPOSE 8080

# JVM 옵션 설정
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -Duser.timezone=Asia/Seoul"

# Spring Boot 실행
CMD sh -c "echo 'Starting Moitz Application...' && java $JAVA_OPTS -jar /app/app.jar"