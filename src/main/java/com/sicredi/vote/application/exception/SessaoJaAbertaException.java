package com.sicredi.vote.application.exception;

import java.util.UUID;

public class SessaoJaAbertaException extends AplicacaoException {
  private final UUID pautaId;

  public SessaoJaAbertaException(UUID pautaId) {
    super(TipoProblema.SESSAO_JA_ABERTA);
    this.pautaId = pautaId;
  }

  public UUID pautaId() {
    return pautaId;
  }
}
