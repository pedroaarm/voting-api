package com.sicredi.vote.adapters.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "sessao")
public class SessaoJpaEntity {
    @Id private UUID id;
    @Column(name = "pauta_id") private UUID pautaId;
    private Instant abertura;
    private Instant fechamento;
    @Column(name = "criada_em") private Instant criadaEm;

    protected SessaoJpaEntity() {}
    public SessaoJpaEntity(UUID id, UUID pautaId, Instant abertura, Instant fechamento, Instant criadaEm) {
        this.id = id; this.pautaId = pautaId; this.abertura = abertura; this.fechamento = fechamento; this.criadaEm = criadaEm;
    }
    public UUID getId() { return id; }
    public UUID getPautaId() { return pautaId; }
    public Instant getAbertura() { return abertura; }
    public Instant getFechamento() { return fechamento; }
    public Instant getCriadaEm() { return criadaEm; }
}
