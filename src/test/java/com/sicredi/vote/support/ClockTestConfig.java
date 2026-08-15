package com.sicredi.vote.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;

/**
 * Overrides the production {@link Clock} bean (see {@code com.sicredi.vote.config.ClockConfig})
 * with a test-controlled {@link AdjustableClock}, so tests can advance time to close a
 * voting session instead of sleeping.
 */
@TestConfiguration
public class ClockTestConfig {

    // Not @Primary: AdjustableClock IS-A Clock, so marking both this bean and
    // primaryClock() below as @Primary would create two ambiguous @Primary
    // candidates for the Clock type. Only the Clock-typed bean needs @Primary;
    // this one is unambiguous by its own concrete type (AdjustableClock).
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
