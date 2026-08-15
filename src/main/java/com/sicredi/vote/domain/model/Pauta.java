package com.sicredi.vote.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class Pauta {
    private final UUID id;
    private final String titulo;
    private final String descricao;
    private final Instant criadaEm;
}
