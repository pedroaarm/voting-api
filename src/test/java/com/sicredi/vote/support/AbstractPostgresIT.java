package com.sicredi.vote.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

// Cache desligado por padrao (NoOp): os ITs que nao exercitam cache nao precisam de Redis.
// O CacheIT sobe seu proprio Redis e sobrescreve spring.cache.type=redis.
// O health indicator do Redis nao e gated pelo spring.cache.type; sem Redis ele deixaria
// /actuator/health como DOWN. Como estes ITs nao sobem Redis, desligamos so esse contribuidor.
@SpringBootTest
@TestPropertySource(
    properties = {"spring.cache.type=none", "management.health.redis.enabled=false"})
public abstract class AbstractPostgresIT {

  @ServiceConnection
  static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));

  static {
    POSTGRES.start();
  }
}
