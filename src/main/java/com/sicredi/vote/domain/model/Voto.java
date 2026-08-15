package com.sicredi.vote.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class Voto {
    private final UUID id;
    private final UUID pautaId;
    private final String associadoId;
    private final OpcaoVoto opcao;
    private final Instant criadoEm;
}
