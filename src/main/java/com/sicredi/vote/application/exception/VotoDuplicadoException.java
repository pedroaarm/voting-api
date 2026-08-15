package com.sicredi.vote.application.exception;

public class VotoDuplicadoException extends AplicacaoException {
  private final String associadoId;

  public VotoDuplicadoException(String associadoId) {
    super(TipoProblema.VOTO_DUPLICADO);
    this.associadoId = associadoId;
  }

  public String associadoId() {
    return associadoId;
  }
}
