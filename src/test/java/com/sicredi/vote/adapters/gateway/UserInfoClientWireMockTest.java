package com.sicredi.vote.adapters.gateway;

import com.sicredi.vote.application.exception.ElegibilidadeIndisponivelException;
import com.sicredi.vote.application.port.out.Elegibilidade;
import com.sicredi.vote.config.ElegibilidadeProperties;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.*;
import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Assertions.*;

class UserInfoClientWireMockTest {

    static WireMockServer wm;
    CircuitBreakerRegistry cbRegistry;
    RetryRegistry retryRegistry;

    @BeforeAll static void startWm() { wm = new WireMockServer(options().dynamicPort()); wm.start(); }
    @AfterAll  static void stopWm()  { wm.stop(); }
    @BeforeEach void resetWm() { wm.resetAll(); }

    private UserInfoClient client() {
        cbRegistry = CircuitBreakerRegistry.of(CircuitBreakerConfig.custom()
            .slidingWindowSize(4).minimumNumberOfCalls(4).failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .recordExceptions(ElegibilidadeIndisponivelException.class).build());
        retryRegistry = RetryRegistry.of(RetryConfig.custom()
            .maxAttempts(2).waitDuration(Duration.ofMillis(10))
            .retryExceptions(ElegibilidadeIndisponivelException.class).build());
        var props = new ElegibilidadeProperties("user-info", "http://localhost:" + wm.port(), Duration.ofSeconds(2));
        return new UserInfoClient(props, cbRegistry, retryRegistry);
    }

    @Test
    void status200AbleRetornaAble() {
        wm.stubFor(get(urlEqualTo("/users/111")).willReturn(okJson("{\"status\":\"ABLE_TO_VOTE\"}")));
        assertThat(client().verificar("111")).isEqualTo(Elegibilidade.ABLE_TO_VOTE);
    }

    @Test
    void status200UnableRetornaUnable() {
        wm.stubFor(get(urlEqualTo("/users/222")).willReturn(okJson("{\"status\":\"UNABLE_TO_VOTE\"}")));
        assertThat(client().verificar("222")).isEqualTo(Elegibilidade.UNABLE_TO_VOTE);
    }

    @Test
    void status404TrataComoInelegivel() {
        wm.stubFor(get(urlEqualTo("/users/000")).willReturn(aResponse().withStatus(404)));
        assertThat(client().verificar("000")).isEqualTo(Elegibilidade.UNABLE_TO_VOTE);
    }

    @Test
    void timeoutResultaEmIndisponivel() {
        wm.stubFor(get(urlEqualTo("/users/999")).willReturn(okJson("{\"status\":\"ABLE_TO_VOTE\"}").withFixedDelay(3000)));
        assertThatThrownBy(() -> client().verificar("999"))
            .isInstanceOf(ElegibilidadeIndisponivelException.class);
    }

    @Test
    void falhas5xxAbremOCircuitoEFailClosed() {
        wm.stubFor(get(urlPathMatching("/users/.*")).willReturn(aResponse().withStatus(500)));
        var c = client();
        // dispara chamadas suficientes para encher a janela e abrir o circuito
        for (int i = 0; i < 6; i++) {
            assertThatThrownBy(() -> c.verificar("5" + Math.random()))
                .isInstanceOf(ElegibilidadeIndisponivelException.class);
        }
        assertThat(cbRegistry.circuitBreaker("userInfo").getState())
            .isIn(CircuitBreaker.State.OPEN, CircuitBreaker.State.FORCED_OPEN);
        // com o circuito aberto, ainda é indisponivel (fail-closed), sem tocar o WireMock
        assertThatThrownBy(() -> c.verificar("depois"))
            .isInstanceOf(ElegibilidadeIndisponivelException.class);
    }
}
