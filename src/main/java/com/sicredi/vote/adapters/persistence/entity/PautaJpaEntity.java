package com.sicredi.vote.adapters.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "pauta")
public class PautaJpaEntity {
  @Id private UUID id;
  private String titulo;
  private String descricao;

  @Column(name = "criada_em")
  private Instant criadaEm;

  protected PautaJpaEntity() {}

  public PautaJpaEntity(UUID id, String titulo, String descricao, Instant criadaEm) {
    this.id = id;
    this.titulo = titulo;
    this.descricao = descricao;
    this.criadaEm = criadaEm;
  }

  public UUID getId() {
    return id;
  }

  public String getTitulo() {
    return titulo;
  }

  public String getDescricao() {
    return descricao;
  }

  public Instant getCriadaEm() {
    return criadaEm;
  }
}
