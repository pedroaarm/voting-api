package com.sicredi.vote.application.exception;

import java.util.UUID;

public class SessaoEmAndamentoException extends RuntimeException {
    public SessaoEmAndamentoException(UUID pautaId) {
        super("Sessão ainda em andamento para a pauta: " + pautaId);
    }
}
