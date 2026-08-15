package com.sicredi.vote.application.exception;

import java.util.UUID;

public class SessaoFechadaException extends AplicacaoException {
  private final UUID pautaId;

  public SessaoFechadaException(UUID pautaId) {
    super(TipoProblema.SESSAO_FECHADA);
    this.pautaId = pautaId;
  }

  public UUID pautaId() {
    return pautaId;
  }
}
