package com.f12.moitz.common.filter;

import com.f12.moitz.application.RateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter implements Filter {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        final String clientIp = httpServletRequest.getHeader("X-Forwarded-For");
        final String userAgent = httpServletRequest.getHeader("User-Agent");
        log.debug("Processing request from IP: {} for URI: {}", clientIp, httpServletRequest.getRequestURI());

        try {
            final ConsumptionProbe probe = rateLimitService.tryConsume(clientIp, userAgent);

            if (!probe.isConsumed()) {
                log.warn("Rate limit exceeded for user: {} | {}", clientIp, userAgent);
                handleRateLimitExceeded(httpServletRequest, httpServletResponse, probe.getNanosToWaitForRefill());
            } else {
                log.debug("Request allowed for user: {} | {}, remaining tokens: {}",
                        clientIp,
                        userAgent,
                        probe.getRemainingTokens()
                );
            }

            final ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpServletRequest);
            chain.doFilter(wrappedRequest, response);
        } catch (Exception e) {
            log.error("Error occurred during rate limiting for IP: {}", clientIp, e);
        }
    }

    private void handleRateLimitExceeded(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final long timeToRefill
    ) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        final Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", "I0002");
        errorResponse.put("message", "Too many requests. Please try again later.");
        errorResponse.put("method", request.getMethod());
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        errorResponse.put("timestamp", LocalDateTime.now().toString());

        response.setHeader("Retry-After", String.valueOf(TimeUnit.NANOSECONDS.toSeconds(timeToRefill)));

        final String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

}
