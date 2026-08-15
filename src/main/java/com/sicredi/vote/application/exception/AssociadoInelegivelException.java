package com.sicredi.vote.application.exception;

public class AssociadoInelegivelException extends RuntimeException {
    public AssociadoInelegivelException(String cpf) {
        super("Associado inelegível para votar");
    }
}
