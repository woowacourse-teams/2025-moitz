package com.f12.moitz.common.filter;

import com.f12.moitz.application.RateLimitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter implements Filter {

    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String clientIp = getClientIpAddress(httpServletRequest);
        log.debug("Processing request from IP: {} for URI: {}", clientIp, httpServletRequest.getRequestURI());

        try {
            boolean allowed = rateLimitService.tryConsume(clientIp);
            if (!allowed) {
                handleRateLimitExceeded(httpServletResponse, clientIp);
            }

            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpServletRequest);
            chain.doFilter(wrappedRequest,response);
        } catch (Exception e) {
            log.error("Error occurred during rate limiting for IP: {}", clientIp, e);

        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED"
        };

        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                if (ip.contains(",")) {
                    ip = ip.split(",")[0].trim();
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }

    private void handleRateLimitExceeded(HttpServletResponse response, String clientIp) throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        long availableTokens = rateLimitService.getAvailableTokens(clientIp);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Rate limit exceeded");
        errorResponse.put("message", "Too many requests. Please try again later.");
        errorResponse.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
        errorResponse.put("availableTokens", availableTokens);
        errorResponse.put("clientIp", clientIp);

        response.setHeader("Retry-After", "3600");

        response.setHeader("X-RateLimit-Limit", "10");
        response.setHeader("X-RateLimit-Remaining", String.valueOf(availableTokens));
        response.setHeader("X-RateLimit-Reset", String.valueOf(System.currentTimeMillis() + 3600000)); // 1시간 후

        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();

        log.warn("Rate limit exceeded for IP: {}. Available tokens: {}", clientIp, availableTokens);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("RateLimitFilter initialized");
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        log.info("RateLimitFilter destroyed");
        Filter.super.destroy();
    }
}
