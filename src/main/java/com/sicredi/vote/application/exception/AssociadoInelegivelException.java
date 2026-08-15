package com.sicredi.vote.application.exception;

public class AssociadoInelegivelException extends RuntimeException {
  public AssociadoInelegivelException() {
    super("Associado inelegível para votar");
  }
}
