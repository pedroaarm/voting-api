package com.sicredi.vote.application.exception;

import java.util.UUID;

public class SessaoJaAbertaException extends RuntimeException {
  public SessaoJaAbertaException(UUID pautaId) {
    super("Sessão já aberta para a pauta: " + pautaId);
  }
}
