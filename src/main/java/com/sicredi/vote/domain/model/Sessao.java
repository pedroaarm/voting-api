package com.sicredi.vote.domain.model;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Sessao {
  private final UUID id;
  private final UUID pautaId;
  private final Instant abertura;
  private final Instant fechamento;

  public static Sessao abrir(UUID pautaId, Instant abertura, int duracaoMinutos) {
    if (duracaoMinutos <= 0) {
      throw new IllegalArgumentException("duracaoMinutos deve ser maior que zero");
    }
    return Sessao.builder()
        .id(UUID.randomUUID())
        .pautaId(pautaId)
        .abertura(abertura)
        .fechamento(abertura.plus(Duration.ofMinutes(duracaoMinutos)))
        .build();
  }

  public boolean estaAberta(Instant momento) {
    return !momento.isBefore(abertura) && momento.isBefore(fechamento);
  }

  public boolean estaEncerrada(Instant momento) {
    return !momento.isBefore(fechamento);
  }
}
