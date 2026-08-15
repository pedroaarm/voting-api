package com.sicredi.vote.application.exception;

import java.util.UUID;

public class PautaNaoEncontradaException extends RuntimeException {
  public PautaNaoEncontradaException(UUID id) {
    super("Pauta não encontrada: " + id);
  }
}
