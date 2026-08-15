package com.sicredi.vote.adapters.persistence.entity;

import com.sicredi.vote.domain.model.OpcaoVoto;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "voto")
public class VotoJpaEntity {
  @Id private UUID id;

  @Column(name = "pauta_id")
  private UUID pautaId;

  @Column(name = "associado_id")
  private String associadoId;

  @Enumerated(EnumType.STRING)
  private OpcaoVoto opcao;

  @Column(name = "criado_em")
  private Instant criadoEm;

  protected VotoJpaEntity() {}

  public VotoJpaEntity(
      UUID id, UUID pautaId, String associadoId, OpcaoVoto opcao, Instant criadoEm) {
    this.id = id;
    this.pautaId = pautaId;
    this.associadoId = associadoId;
    this.opcao = opcao;
    this.criadoEm = criadoEm;
  }

  public UUID getId() {
    return id;
  }

  public UUID getPautaId() {
    return pautaId;
  }

  public String getAssociadoId() {
    return associadoId;
  }

  public OpcaoVoto getOpcao() {
    return opcao;
  }

  public Instant getCriadoEm() {
    return criadoEm;
  }
}
