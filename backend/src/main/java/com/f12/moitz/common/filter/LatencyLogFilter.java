package com.f12.moitz.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LatencyLogFilter implements Filter {

  @Override
  public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
    final long t0 = System.nanoTime();
    try { chain.doFilter(req, res); }
    finally {
      final long ms = (System.nanoTime()-t0)/1_000_000;
      log.info("LATENCY totalMs={}", ms);
    }
  }
}
