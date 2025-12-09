#!/bin/bash
set -e

echo '========================================'
echo 'Moitz Application Starting...'
echo '========================================'

# CloudWatch Agent 시작
echo 'Starting CloudWatch Agent...'
if [ -f '/opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl' ]; then
  /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
    -a fetch-config -m ec2 -s \
    -c file:/opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json &
  echo '✓ CloudWatch Agent started'
else
  echo '⚠ CloudWatch Agent not found, skipping...'
fi

# 환경 변수 확인
echo '----------------------------------------'
echo "Java Version: $(java -version 2>&1 | head -n 1)"
echo "Spring Profile: ${SPRING_PROFILES_ACTIVE:-default}"
echo "Redis Host: ${REDIS_HOST:-localhost}"
echo "Redis Port: ${REDIS_PORT:-6379}"
echo "Timezone: ${TZ:-UTC}"
echo '----------------------------------------'

# Spring Boot 시작
echo 'Starting Spring Boot Application...'
echo '========================================'

# JVM 옵션 설정 (JDK 21 최적화)
JAVA_OPTS="${JAVA_OPTS} -XX:+UseContainerSupport"
JAVA_OPTS="${JAVA_OPTS} -XX:MaxRAMPercentage=75.0"
JAVA_OPTS="${JAVA_OPTS} -XX:+UseZGC"
JAVA_OPTS="${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom"
JAVA_OPTS="${JAVA_OPTS} -Duser.timezone=${TZ}"

# Spring Boot 실행
exec java ${JAVA_OPTS} -jar /app/app.jar "$@"