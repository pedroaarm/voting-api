package com.sicredi.vote.adapters.persistence;

import com.sicredi.vote.adapters.persistence.mapper.VotoMapper;
import com.sicredi.vote.adapters.persistence.repository.VotoJpaRepository;
import com.sicredi.vote.application.exception.VotoDuplicadoException;
import com.sicredi.vote.application.port.out.VotoRepository;
import com.sicredi.vote.domain.model.OpcaoVoto;
import com.sicredi.vote.domain.model.Voto;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class VotoRepositoryAdapter implements VotoRepository {
  private final VotoJpaRepository jpa;

  public VotoRepositoryAdapter(VotoJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public Voto salvar(Voto voto) {
    try {
      return VotoMapper.toDomain(jpa.saveAndFlush(VotoMapper.toEntity(voto)));
    } catch (DataIntegrityViolationException e) {
      throw new VotoDuplicadoException(voto.getAssociadoId());
    }
  }

  @Override
  public long contar(UUID pautaId, OpcaoVoto opcao) {
    return jpa.countByPautaIdAndOpcao(pautaId, opcao);
  }
}
