package com.sicredi.vote.adapters.persistence;

import com.sicredi.vote.adapters.persistence.mapper.SessaoMapper;
import com.sicredi.vote.adapters.persistence.repository.SessaoJpaRepository;
import com.sicredi.vote.application.exception.SessaoJaAbertaException;
import com.sicredi.vote.application.port.out.SessaoRepository;
import com.sicredi.vote.domain.model.Sessao;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class SessaoRepositoryAdapter implements SessaoRepository {
  private final SessaoJpaRepository jpa;

  public SessaoRepositoryAdapter(SessaoJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public Sessao salvar(Sessao sessao) {
    try {
      return SessaoMapper.toDomain(jpa.saveAndFlush(SessaoMapper.toEntity(sessao)));
    } catch (DataIntegrityViolationException e) {
      throw new SessaoJaAbertaException(sessao.getPautaId());
    }
  }

  @Override
  public Optional<Sessao> buscarPorPauta(UUID pautaId) {
    return jpa.findByPautaId(pautaId).map(SessaoMapper::toDomain);
  }

  @Override
  public boolean existePorPauta(UUID pautaId) {
    return jpa.existsByPautaId(pautaId);
  }
}
