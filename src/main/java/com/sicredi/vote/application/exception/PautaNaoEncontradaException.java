package com.sicredi.vote.application.exception;

import java.util.UUID;

public class PautaNaoEncontradaException extends AplicacaoException {
  private final UUID id;

  public PautaNaoEncontradaException(UUID id) {
    super(TipoProblema.PAUTA_NAO_ENCONTRADA);
    this.id = id;
  }

  public UUID id() {
    return id;
  }
}
