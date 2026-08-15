package com.sicredi.vote.adapters.persistence.mapper;

import com.sicredi.vote.adapters.persistence.entity.SessaoJpaEntity;
import com.sicredi.vote.domain.model.Sessao;

public final class SessaoMapper {
  private SessaoMapper() {}

  public static SessaoJpaEntity toEntity(Sessao s) {
    return new SessaoJpaEntity(
        s.getId(), s.getPautaId(), s.getAbertura(), s.getFechamento(), s.getAbertura());
  }

  public static Sessao toDomain(SessaoJpaEntity e) {
    return Sessao.builder()
        .id(e.getId())
        .pautaId(e.getPautaId())
        .abertura(e.getAbertura())
        .fechamento(e.getFechamento())
        .build();
  }
}
