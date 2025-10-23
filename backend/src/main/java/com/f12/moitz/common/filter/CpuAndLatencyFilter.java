package com.f12.moitz.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CpuAndLatencyFilter implements Filter {

  private final ThreadMXBean tmx = ManagementFactory.getThreadMXBean();

  @Override
  public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain) throws IOException, ServletException {
    if (tmx.isThreadCpuTimeSupported() && !tmx.isThreadCpuTimeEnabled()) {
      tmx.setThreadCpuTimeEnabled(true); // 필요 시 활성화
    }

    final long tid = Thread.currentThread().getId();
    final long wallStartNs = System.nanoTime();
    final long cpuStartNs  = tmx.isThreadCpuTimeSupported() ? tmx.getCurrentThreadCpuTime() : -1L;
    final String rid = UUID.randomUUID().toString().substring(0,8); // traceId 대용

    try {
      chain.doFilter(req, res);
    } finally {
      final long wallMs = (System.nanoTime() - wallStartNs) / 1_000_000;
      long cpuMs = -1;
      if (tmx.isThreadCpuTimeSupported()) {
        cpuMs = (tmx.getCurrentThreadCpuTime() - cpuStartNs) / 1_000_000;
      }
      // 요청 구간 CPU 점유율(해당 스레드 기준)
      final Double cpuBusyPct = (cpuMs >= 0 && wallMs > 0) ? (cpuMs * 100.0 / wallMs) : null;

      log.info("REQ rid={} tid={} wallMs={} cpuMs={} cpuBusy%={}",
          rid, tid, wallMs, cpuMs, cpuBusyPct);
    }
  }
}
