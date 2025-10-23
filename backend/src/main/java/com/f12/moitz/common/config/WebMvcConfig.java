package com.f12.moitz.common.config;

import com.f12.moitz.common.filter.EndPointLoggingFilter;
import com.f12.moitz.common.filter.RateLimitFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final RateLimitFilter rateLimitFilter;
    private final EndPointLoggingFilter endPointLoggingFilter;

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*");
    }

    @Bean
    public FilterRegistrationBean<EndPointLoggingFilter> endPointLoggingFilterFilterRegistration() {
        final FilterRegistrationBean<EndPointLoggingFilter> registration = new FilterRegistrationBean<>();

        registration.setFilter(endPointLoggingFilter);
        registration.addUrlPatterns("/locations/*");
        registration.addUrlPatterns("/recommendations/*");
        registration.setName("endPointLoggingFilter");
        registration.setOrder(1);

        return registration;
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilterRegistration() {
        final FilterRegistrationBean<RateLimitFilter> registration = new FilterRegistrationBean<>();

        registration.setFilter(rateLimitFilter);
        registration.addUrlPatterns("/recommendations/*");
        registration.setName("rateLimitFilter");
        registration.setOrder(2);

        return registration;
    }

}
