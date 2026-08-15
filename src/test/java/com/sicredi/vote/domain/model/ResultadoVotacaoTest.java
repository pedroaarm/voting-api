package com.sicredi.vote.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ResultadoVotacaoTest {

  private final UUID pauta = UUID.randomUUID();

  @Test
  void aprovadaQuandoSimMaiorQueNao() {
    var r = ResultadoVotacao.apurar(pauta, 3, 1);
    assertThat(r.getStatus()).isEqualTo(StatusResultado.APROVADA);
    assertThat(r.getTotal()).isEqualTo(4);
  }

  @Test
  void rejeitadaQuandoNaoMaiorQueSim() {
    var r = ResultadoVotacao.apurar(pauta, 1, 5);
    assertThat(r.getStatus()).isEqualTo(StatusResultado.REJEITADA);
  }

  @Test
  void empateQuandoIguais() {
    var r = ResultadoVotacao.apurar(pauta, 2, 2);
    assertThat(r.getStatus()).isEqualTo(StatusResultado.EMPATE);
  }

  @Test
  void empateComZeroVotos() {
    var r = ResultadoVotacao.apurar(pauta, 0, 0);
    assertThat(r.getStatus()).isEqualTo(StatusResultado.EMPATE);
    assertThat(r.getTotal()).isZero();
  }
}
