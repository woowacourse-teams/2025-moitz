package com.f12.moitz.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Around("@annotation(postMapping)")
    public Object logPostMappingExecutionTime(ProceedingJoinPoint joinPoint, PostMapping postMapping) throws Throwable {
        return logExecutionTime(joinPoint);
    }

    // 공통 로깅 로직
    private Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;

        log.info("[{}] executed in {} ms", joinPoint.getSignature(), duration);
        return result;
    }
}
