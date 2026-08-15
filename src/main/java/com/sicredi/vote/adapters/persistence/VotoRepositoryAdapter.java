package com.sicredi.vote.adapters.persistence;

import com.sicredi.vote.adapters.persistence.mapper.VotoMapper;
import com.sicredi.vote.adapters.persistence.repository.VotoJpaRepository;
import com.sicredi.vote.application.port.out.VotoRepository;
import com.sicredi.vote.domain.model.OpcaoVoto;
import com.sicredi.vote.domain.model.Voto;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public class VotoRepositoryAdapter implements VotoRepository {
    private final VotoJpaRepository jpa;
    public VotoRepositoryAdapter(VotoJpaRepository jpa) { this.jpa = jpa; }

    @Override public Voto salvar(Voto voto) {
        try {
            return VotoMapper.toDomain(jpa.saveAndFlush(VotoMapper.toEntity(voto)));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new com.sicredi.vote.application.exception.VotoDuplicadoException(voto.getAssociadoId());
        }
    }
    @Override public long contar(UUID pautaId, OpcaoVoto opcao) {
        return jpa.countByPautaIdAndOpcao(pautaId, opcao);
    }
}
