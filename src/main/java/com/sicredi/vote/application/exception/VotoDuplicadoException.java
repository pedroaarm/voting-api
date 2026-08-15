package com.sicredi.vote.application.exception;

public class VotoDuplicadoException extends RuntimeException {
    public VotoDuplicadoException(String associadoId) {
        super("Associado já votou: " + associadoId);
    }
}
