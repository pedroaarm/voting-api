package com.sicredi.vote.adapters.persistence.mapper;

import com.sicredi.vote.adapters.persistence.entity.PautaJpaEntity;
import com.sicredi.vote.domain.model.Pauta;

public final class PautaMapper {
    private PautaMapper() {}
    public static PautaJpaEntity toEntity(Pauta p) {
        return new PautaJpaEntity(p.getId(), p.getTitulo(), p.getDescricao(), p.getCriadaEm());
    }
    public static Pauta toDomain(PautaJpaEntity e) {
        return Pauta.builder().id(e.getId()).titulo(e.getTitulo())
            .descricao(e.getDescricao()).criadaEm(e.getCriadaEm()).build();
    }
}
