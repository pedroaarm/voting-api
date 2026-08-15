package com.sicredi.vote.application.exception;

import java.util.UUID;

public class SessaoFechadaException extends RuntimeException {
  public SessaoFechadaException(UUID pautaId) {
    super("Sessão fechada para a pauta: " + pautaId);
  }
}
