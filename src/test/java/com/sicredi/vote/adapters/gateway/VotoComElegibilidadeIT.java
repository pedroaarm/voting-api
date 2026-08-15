package com.sicredi.vote.adapters.gateway;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.sicredi.vote.support.AbstractPostgresIT;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

  private HttpStatusCode votar(String pautaId, String assoc, String cpf) {
    return rest()
        .post()
        .uri("/api/v1/pautas/{id}/votos", pautaId)
        .contentType(MediaType.APPLICATION_JSON)
        .body("{\"associadoId\":\"" + assoc + "\",\"cpf\":\"" + cpf + "\",\"opcao\":\"SIM\"}")
        .retrieve()
        .onStatus(s -> true, (rq, rs) -> {})
        .toBodilessEntity()
        .getStatusCode();
  }

  @Test
  void associadoElegivelConsegueVotar() {
    wm.stubFor(get(urlEqualTo("/users/111")).willReturn(okJson("{\"status\":\"ABLE_TO_VOTE\"}")));
    assertThat(votar(abrirPautaComSessao(), "a1", "111")).isEqualTo(HttpStatus.CREATED);
  }

  @Test
  void associadoInelegivelRecebe422() {
    wm.stubFor(get(urlEqualTo("/users/222")).willReturn(okJson("{\"status\":\"UNABLE_TO_VOTE\"}")));
    // Spring Framework 7 (Boot 4.1) splits code 422 into two non-equal enum constants
    // (deprecated UNPROCESSABLE_ENTITY vs. canonical UNPROCESSABLE_CONTENT); compare
    // the numeric status code so the assertion is resilient to which one is resolved.
    assertThat(votar(abrirPautaComSessao(), "a2", "222").value()).isEqualTo(422);
  }

  @Test
  void servicoForaDoArRecusaCom503() {
    wm.stubFor(get(urlPathMatching("/users/.*")).willReturn(aResponse().withStatus(500)));
    assertThat(votar(abrirPautaComSessao(), "a3", "333")).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }
}
