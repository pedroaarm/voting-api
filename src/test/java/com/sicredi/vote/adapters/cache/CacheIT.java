package com.sicredi.vote.adapters.cache;

import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.sicredi.vote.adapters.persistence.repository.PautaJpaRepository;
import com.sicredi.vote.application.port.out.Elegibilidade;
import com.sicredi.vote.application.port.out.PautaRepository;
import com.sicredi.vote.application.port.out.VerificadorElegibilidade;
import com.sicredi.vote.config.CacheConfig;
import com.sicredi.vote.domain.model.Pauta;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
class CacheIT {

  @ServiceConnection
  static final PostgreSQLContainer<?> POSTGRES =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"));

  static final GenericContainer<?> REDIS =
      new GenericContainer<>(DockerImageName.parse("redis:7-alpine")).withExposedPorts(6379);

  static final WireMockServer WM = new WireMockServer(options().dynamicPort());

  static {
    POSTGRES.start();
    REDIS.start();
    WM.start();
  }

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry registry) {
    registry.add("spring.cache.type", () -> "redis");
    registry.add("spring.data.redis.host", REDIS::getHost);
    registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
    registry.add("vote.elegibilidade.provider", () -> "user-info");
    registry.add("vote.elegibilidade.url", () -> "http://localhost:" + WM.port());
  }

  @AfterAll
  static void stopWm() {
    WM.stop();
  }

  @Autowired VerificadorElegibilidade elegibilidade;
  @Autowired PautaRepository pautas;
  @Autowired CacheManager cacheManager;
  @MockitoSpyBean PautaJpaRepository jpaSpy;

  // Nao limpamos o cache aqui de proposito: o Redis e um container proprio desta classe (fresco)
  // e cada teste usa uma regiao/chave disjunta. Um clear() do RedisCache (SCAN + DEL) e
  // eventualmente consistente e, disparado no setup, chegaria durante o corpo do teste, apagando
  // o que ele acabou de gravar -> flakiness. Sem estado compartilhado entre testes, nao ha o que
  // limpar; so isolamos as interacoes do spy.
  @BeforeEach
  void limpar() {
    WM.resetAll();
    clearInvocations(jpaSpy);
  }

  // Escrita e leitura do cache trafegam em conexoes Lettuce distintas, entao o valor gravado leva
  // alguns ms para ficar visivel (nao ha read-your-writes). Esperamos a condicao estabilizar antes
  // de asserir o efeito de cache, evitando flakiness.
  private void aguarda(BooleanSupplier condicao) {
    await()
        .atMost(Duration.ofSeconds(5))
        .pollInterval(Duration.ofMillis(20))
        .until(condicao::getAsBoolean);
  }

  private boolean cacheContem(String cache, Object chave) {
    return cacheManager.getCache(cache).get(chave) != null;
  }

  @Test
  void elegibilidadeEhCacheadaPorCpf() {
    WM.stubFor(
        WireMock.get(urlEqualTo("/users/12345678900"))
            .willReturn(okJson("{\"status\":\"ABLE_TO_VOTE\"}")));

    assertThat(elegibilidade.verificar("12345678900")).isEqualTo(Elegibilidade.ABLE_TO_VOTE);
    aguarda(() -> cacheContem(CacheConfig.CACHE_ELEGIBILIDADE, "12345678900"));

    assertThat(elegibilidade.verificar("12345678900")).isEqualTo(Elegibilidade.ABLE_TO_VOTE);

    // segunda chamada veio do cache -> o servico externo foi consultado uma unica vez
    WM.verify(1, getRequestedFor(urlEqualTo("/users/12345678900")));
  }

  @Test
  void pautaPorIdEhCacheadaComRoundTripNoRedis() {
    Pauta salva =
        pautas.salvar(
            Pauta.builder()
                .id(UUID.randomUUID())
                .titulo("Reforma do estatuto")
                .descricao("descricao")
                .criadaEm(Instant.parse("2026-08-14T13:30:00Z"))
                .build());
    clearInvocations(jpaSpy);

    pautas.buscarPorId(salva.getId()); // popula o cache
    aguarda(() -> cacheContem(CacheConfig.CACHE_PAUTA, salva.getId()));
    clearInvocations(jpaSpy);

    Optional<Pauta> doCache = pautas.buscarPorId(salva.getId()); // vem do Redis

    verify(jpaSpy, never()).findById(salva.getId()); // veio do cache, sem tocar o banco
    assertThat(doCache).isPresent();
    // valor desserializado do Redis mantem os campos (prova o round-trip JSON do dominio)
    assertThat(doCache.get().getTitulo()).isEqualTo("Reforma do estatuto");
    assertThat(doCache.get().getCriadaEm()).isEqualTo(Instant.parse("2026-08-14T13:30:00Z"));
  }

  @Test
  void listaDePautasEhCacheadaEInvalidadaAoSalvar() {
    pautas.listarTodas(); // popula o cache da lista
    aguarda(() -> cacheContem(CacheConfig.CACHE_PAUTAS_LISTA, "todas"));
    clearInvocations(jpaSpy);

    pautas.listarTodas(); // vem do cache
    verify(jpaSpy, never()).findAll();

    pautas.salvar(
        Pauta.builder().id(UUID.randomUUID()).titulo("Nova pauta").criadaEm(Instant.now()).build());
    aguarda(() -> !cacheContem(CacheConfig.CACHE_PAUTAS_LISTA, "todas")); // @CacheEvict propagou
    clearInvocations(jpaSpy);

    pautas.listarTodas(); // cache foi invalidado -> consulta o banco de novo
    verify(jpaSpy, times(1)).findAll();
  }
}
