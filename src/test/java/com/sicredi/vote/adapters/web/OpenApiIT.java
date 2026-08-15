package com.sicredi.vote.adapters.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.sicredi.vote.support.AbstractPostgresIT;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenApiIT extends AbstractPostgresIT {

  @LocalServerPort int port;

  @Test
  void apiDocsExpoeOsEndpoints() throws Exception {
    var client = HttpClient.newHttpClient();
    var request =
        HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/v3/api-docs"))
            .GET()
            .build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    String json = response.body();
    assertThat(json).contains("/api/v1/pautas");
    assertThat(json).contains("/api/v1/pautas/{pautaId}/votos");
    assertThat(json).contains("/api/v1/pautas/{pautaId}/resultado");
    assertThat(json).contains("Cadastra uma nova pauta");
    assertThat(json).contains("Registra um voto do associado na pauta");
    assertThat(json).contains("Consulta o resultado da votacao de uma pauta");
  }
}
