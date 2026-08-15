package com.sicredi.vote.application.exception;

public class ElegibilidadeIndisponivelException extends AplicacaoException {
  public ElegibilidadeIndisponivelException() {
    super(TipoProblema.ELEGIBILIDADE_INDISPONIVEL);
  }

  public ElegibilidadeIndisponivelException(Throwable causa) {
    super(TipoProblema.ELEGIBILIDADE_INDISPONIVEL, causa);
  }
}
