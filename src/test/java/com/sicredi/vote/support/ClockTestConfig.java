package com.sicredi.vote.support;

import java.time.Clock;
import java.time.Instant;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class ClockTestConfig {

  @Bean
  public AdjustableClock testClock() {
    return new AdjustableClock(Instant.parse("2026-08-13T10:00:00Z"));
  }

  @Bean
  @Primary
  public Clock primaryClock(AdjustableClock c) {
    return c;
  }
}
