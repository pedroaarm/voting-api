package com.sicredi.vote.adapters.persistence.mapper;

import com.sicredi.vote.adapters.persistence.entity.VotoJpaEntity;
import com.sicredi.vote.domain.model.Voto;

public final class VotoMapper {
    private VotoMapper() {}
    public static VotoJpaEntity toEntity(Voto v) {
        return new VotoJpaEntity(v.getId(), v.getPautaId(), v.getAssociadoId(), v.getOpcao(), v.getCriadoEm());
    }
    public static Voto toDomain(VotoJpaEntity e) {
        return Voto.builder().id(e.getId()).pautaId(e.getPautaId())
            .associadoId(e.getAssociadoId()).opcao(e.getOpcao()).criadoEm(e.getCriadoEm()).build();
    }
}
