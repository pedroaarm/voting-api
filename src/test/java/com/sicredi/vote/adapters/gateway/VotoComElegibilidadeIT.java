package com.sicredi.vote.adapters.gateway;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.sicredi.vote.support.AbstractPostgresIT;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "vote.elegibilidade.provider=user-info")
class VotoComElegibilidadeIT extends AbstractPostgresIT {

  static WireMockServer wm = new WireMockServer(options().dynamicPort());

  @BeforeAll
  static void start() {
    wm.start();
  }

  @AfterAll
  static void stop() {
    wm.stop();
  }

  @BeforeEach
  void reset() {
    wm.resetAll();
  }

  @DynamicPropertySource
  static void props(DynamicPropertyRegistry r) {
    r.add("vote.elegibilidade.url", () -> "http://localhost:" + wm.port());
    r.add("vote.elegibilidade.timeout", () -> "2s");
  }

  @LocalServerPort int port;

  private RestClient rest() {
    return RestClient.create("http://localhost:" + port);
  }

  private String abrirPautaComSessao() {
    var rc = rest();
    var pauta =
        rc.post()
            .uri("/api/v1/pautas")
            .contentType(MediaType.APPLICATION_JSON)
            .body("{\"titulo\":\"P\"}")
            .retrieve()
            .toEntity(String.class);
    String id = com.jayway.jsonpath.JsonPath.read(pauta.getBody(), "$.id");
    rc.post()
        .uri("/api/v1/pautas/{id}/sessoes", id)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{}")
        .retrieve()
        .toBodilessEntity();
    return id;
  }
}
