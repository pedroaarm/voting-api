package com.sicredi.vote.observability;

import static org.assertj.core.api.Assertions.assertThat;

import com.sicredi.vote.support.AbstractPostgresIT;
import java.net.URI;
import java.net.http.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ActuatorIT extends AbstractPostgresIT {

  @LocalServerPort int port;

  private String get(String path) throws Exception {
    var resp =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    return resp.statusCode() + "|" + resp.body();
  }

  @Test
  void healthEstaUp() throws Exception {
    assertThat(get("/actuator/health")).contains("200").contains("\"status\":\"UP\"");
  }

  @Test
  void prometheusExpoeMetricasDeNegocio() throws Exception {
    assertThat(get("/actuator/prometheus")).contains("200");
  }
}
