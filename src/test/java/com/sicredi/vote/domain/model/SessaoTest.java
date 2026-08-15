package com.sicredi.vote.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class SessaoTest {

  private final UUID pauta = UUID.randomUUID();
  private final Instant t0 = Instant.parse("2026-08-12T10:00:00Z");

  @Test
  void abertaNoInstanteDeAbertura() {
    Sessao s = Sessao.abrir(pauta, t0, 1);
    assertThat(s.estaAberta(t0)).isTrue();
  }

  @Test
  void abertaUmMilissegundoAntesDoFechamento() {
    Sessao s = Sessao.abrir(pauta, t0, 1);
    assertThat(s.estaAberta(s.getFechamento().minusMillis(1))).isTrue();
  }

  @Test
  void fechadaExatamenteNoFechamento() {
    Sessao s = Sessao.abrir(pauta, t0, 1);
    assertThat(s.estaAberta(s.getFechamento())).isFalse();
    assertThat(s.estaEncerrada(s.getFechamento())).isTrue();
  }

  @Test
  void naoAbertaAntesDaAbertura() {
    Sessao s = Sessao.abrir(pauta, t0, 1);
    assertThat(s.estaAberta(t0.minusMillis(1))).isFalse();
  }

  @Test
  void fechamentoEhAberturaMaisDuracao() {
    Sessao s = Sessao.abrir(pauta, t0, 5);
    assertThat(s.getFechamento()).isEqualTo(t0.plus(Duration.ofMinutes(5)));
  }

  @Test
  void duracaoInvalidaEhRejeitada() {
    assertThatThrownBy(() -> Sessao.abrir(pauta, t0, 0))
        .isInstanceOf(IllegalArgumentException.class);
    assertThatThrownBy(() -> Sessao.abrir(pauta, t0, -3))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
