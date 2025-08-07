package com.f12.moitz.common.config;

import com.f12.moitz.common.error.exception.ExternalApiException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.context.RetryContextSupport;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfig {

    @Bean
    public RetryOperationsInterceptor getGeminiOperationInterceptor() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate
                .setRetryPolicy(new RetryPolicy() {
                    @Override
                    public boolean canRetry(final RetryContext retryContext) {
                        Throwable lastThrowable = retryContext.getLastThrowable();
                        return checkRetry(lastThrowable) && retryContext.getRetryCount() < 3;
                    }

                    @Override
                    public RetryContext open(final RetryContext retryContext) {
                        return new RetryContextSupport(retryContext);
                    }

                    @Override
                    public void close(final RetryContext retryContext) {
                    }

                    @Override
                    public void registerThrowable(final RetryContext retryContext, final Throwable throwable) {
                        ((RetryContextSupport) retryContext).registerThrowable(throwable);
                    }
                });

        return RetryInterceptorBuilder.stateless()
                .retryOperations(retryTemplate)
                .build();
    }

    private boolean checkRetry(Throwable throwable) {
        if (throwable instanceof ExternalApiException e) {
            return e.getErrorCode().getCanRetry();
        }
        return false;
    }
}
