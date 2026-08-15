package com.sicredi.vote.application.exception;

import java.util.UUID;

public class SessaoEmAndamentoException extends AplicacaoException {
  private final UUID pautaId;

  public SessaoEmAndamentoException(UUID pautaId) {
    super(TipoProblema.SESSAO_EM_ANDAMENTO);
    this.pautaId = pautaId;
  }

  public UUID pautaId() {
    return pautaId;
  }
}
