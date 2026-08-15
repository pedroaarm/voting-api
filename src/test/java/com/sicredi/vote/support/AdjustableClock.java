package com.sicredi.vote.support;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class AdjustableClock extends Clock {

  private Instant instant;

  public AdjustableClock(Instant inicial) {
    this.instant = inicial;
  }

  public void avancar(Duration d) {
    this.instant = this.instant.plus(d);
  }

  @Override
  public Instant instant() {
    return instant;
  }

  @Override
  public ZoneId getZone() {
    return ZoneOffset.UTC;
  }

  @Override
  public Clock withZone(ZoneId zone) {
    return this;
  }
}
