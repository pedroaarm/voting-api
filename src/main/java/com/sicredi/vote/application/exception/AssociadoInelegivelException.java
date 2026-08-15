package com.sicredi.vote.application.exception;

public class AssociadoInelegivelException extends AplicacaoException {
  public AssociadoInelegivelException() {
    super(TipoProblema.ASSOCIADO_INELEGIVEL);
  }
}
