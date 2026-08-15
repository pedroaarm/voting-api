package com.sicredi.vote.application.exception;

public abstract class AplicacaoException extends RuntimeException {

  private final TipoProblema tipoProblema;

  protected AplicacaoException(TipoProblema tipoProblema) {
    super(tipoProblema.detailKey());
    this.tipoProblema = tipoProblema;
  }

  protected AplicacaoException(TipoProblema tipoProblema, Throwable causa) {
    super(tipoProblema.detailKey(), causa);
    this.tipoProblema = tipoProblema;
  }

  public TipoProblema tipoProblema() {
    return tipoProblema;
  }
}
