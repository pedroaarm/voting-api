package com.sicredi.vote.adapters.web;

import com.sicredi.vote.support.AbstractPostgresIT;
import com.sicredi.vote.support.AdjustableClock;
import com.sicredi.vote.support.ClockTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end integration test exercising the full REST API over HTTP against a real
 * (Testcontainers) Postgres instance, using an {@link AdjustableClock} to close a
 * time-bounded voting session deterministically, without sleeping.
 *
 * <p>HTTP client: {@link RestClient} (spring-web, already on the classpath via
 * spring-boot-starter-web/webmvc-test transitively) — resolved fine on Boot 4.1, no
 * JDK HttpClient fallback needed.
 *
 * <p>JSON extraction: {@code com.jayway.jsonpath.JsonPath}, transitive from
 * spring-boot-starter-test — resolved fine, no Jackson ObjectMapper fallback needed.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(ClockTestConfig.class)
class ApiFluxoIT extends AbstractPostgresIT {

    @LocalServerPort int port;
    @Autowired AdjustableClock clock;

    private RestClient client() {
        return RestClient.create("http://localhost:" + port);
    }

    @Test
    void fluxoCompletoCadastrarAbrirVotarResultado() {
        var rest = client();

        // cadastrar pauta
        var pauta = rest.post().uri("/api/v1/pautas").contentType(MediaType.APPLICATION_JSON)
            .body("{\"titulo\":\"Reforma\",\"descricao\":\"d\"}").retrieve().toEntity(String.class);
        assertThat(pauta.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String pautaId = com.jayway.jsonpath.JsonPath.read(pauta.getBody(), "$.id");

        // abrir sessao (1 min)
        rest.post().uri("/api/v1/pautas/{id}/sessoes", pautaId).contentType(MediaType.APPLICATION_JSON)
            .body("{\"duracaoMinutos\":1}").retrieve().toBodilessEntity();

        // votar SIM e NAO por associados distintos
        rest.post().uri("/api/v1/pautas/{id}/votos", pautaId).contentType(MediaType.APPLICATION_JSON)
            .body("{\"associadoId\":\"a1\",\"cpf\":\"1\",\"opcao\":\"SIM\"}").retrieve().toBodilessEntity();
        rest.post().uri("/api/v1/pautas/{id}/votos", pautaId).contentType(MediaType.APPLICATION_JSON)
            .body("{\"associadoId\":\"a2\",\"cpf\":\"2\",\"opcao\":\"SIM\"}").retrieve().toBodilessEntity();
        rest.post().uri("/api/v1/pautas/{id}/votos", pautaId).contentType(MediaType.APPLICATION_JSON)
            .body("{\"associadoId\":\"a3\",\"cpf\":\"3\",\"opcao\":\"NAO\"}").retrieve().toBodilessEntity();

        // resultado antes de fechar -> 409
        var emAndamento = rest.get().uri("/api/v1/pautas/{id}/resultado", pautaId)
            .retrieve().onStatus(s -> true, (rq, rs) -> {}).toEntity(String.class);
        assertThat(emAndamento.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        // fecha a sessao avancando o relogio (sem sleep)
        clock.avancar(Duration.ofMinutes(2));

        var resultado = rest.get().uri("/api/v1/pautas/{id}/resultado", pautaId).retrieve().toEntity(String.class);
        assertThat(resultado.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((String) com.jayway.jsonpath.JsonPath.read(resultado.getBody(), "$.status")).isEqualTo("APROVADA");
        assertThat(((Number) com.jayway.jsonpath.JsonPath.read(resultado.getBody(), "$.totalSim")).intValue()).isEqualTo(2);
    }

    @Test
    void votoDuplicadoRetorna409() {
        var rest = client();
        var pauta = rest.post().uri("/api/v1/pautas").contentType(MediaType.APPLICATION_JSON)
            .body("{\"titulo\":\"P\"}").retrieve().toEntity(String.class);
        String pautaId = com.jayway.jsonpath.JsonPath.read(pauta.getBody(), "$.id");
        rest.post().uri("/api/v1/pautas/{id}/sessoes", pautaId).contentType(MediaType.APPLICATION_JSON)
            .body("{}").retrieve().toBodilessEntity();
        rest.post().uri("/api/v1/pautas/{id}/votos", pautaId).contentType(MediaType.APPLICATION_JSON)
            .body("{\"associadoId\":\"a1\",\"cpf\":\"1\",\"opcao\":\"SIM\"}").retrieve().toBodilessEntity();

        var dup = rest.post().uri("/api/v1/pautas/{id}/votos", pautaId).contentType(MediaType.APPLICATION_JSON)
            .body("{\"associadoId\":\"a1\",\"cpf\":\"1\",\"opcao\":\"NAO\"}")
            .retrieve().onStatus(s -> true, (rq, rs) -> {}).toEntity(String.class);
        assertThat(dup.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}
