package com.f12.moitz;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
public class LoggingTest {

    @Test
    @DisplayName("로깅이 파일에 저장되는지 테스트")
    void LoggingTest() {
        // Given
        log.debug("DEBUG");
        log.info("INFO");
        log.warn("WARN");
        log.error("ERROR");
        // When

        // Then
    }

    @Test
    @DisplayName("로그 압축(zip)이 생성되는지 확인")
    void testLogRollingZipCreation() throws InterruptedException {
        // Given: 약 10MB 이상 로그 생성
        log.info("압축 유도용 로그 생성 시작");

        for (int i = 0; i < 10000; i++) { // 약 1KB × 10000 = 약 10MB
            log.info("로그 인덱스 [{}] - {}", i, "x".repeat(1024));
        }

        log.info("로그 생성 완료");

        // 로그 처리 대기 시간 (백그라운드 flush 대기)
        Thread.sleep(3000);

        // When: .zip 파일 존재 여부 확인
        File logDir = new File("./logs/info");
        File[] zipFiles = logDir.listFiles((dir, name) -> name.endsWith(".zip"));

        // Then
        assertThat(zipFiles)
                .isNotNull()
                .isNotEmpty();

        Arrays.stream(zipFiles).forEach(file -> {
            System.out.println("압축 로그 파일 확인됨: " + file.getName());
        });
    }
}
