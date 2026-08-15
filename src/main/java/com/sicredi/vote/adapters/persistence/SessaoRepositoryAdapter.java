package com.sicredi.vote.adapters.persistence;

import com.sicredi.vote.adapters.persistence.mapper.SessaoMapper;
import com.sicredi.vote.adapters.persistence.repository.SessaoJpaRepository;
import com.sicredi.vote.application.port.out.SessaoRepository;
import com.sicredi.vote.domain.model.Sessao;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SessaoRepositoryAdapter implements SessaoRepository {
    private final SessaoJpaRepository jpa;
    public SessaoRepositoryAdapter(SessaoJpaRepository jpa) { this.jpa = jpa; }

    @Override public Sessao salvar(Sessao sessao) {
        try {
            return SessaoMapper.toDomain(jpa.saveAndFlush(SessaoMapper.toEntity(sessao)));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new com.sicredi.vote.application.exception.SessaoJaAbertaException(sessao.getPautaId());
        }
    }
    @Override public Optional<Sessao> buscarPorPauta(UUID pautaId) {
        return jpa.findByPautaId(pautaId).map(SessaoMapper::toDomain);
    }
    @Override public boolean existePorPauta(UUID pautaId) {
        return jpa.existsByPautaId(pautaId);
    }
}
