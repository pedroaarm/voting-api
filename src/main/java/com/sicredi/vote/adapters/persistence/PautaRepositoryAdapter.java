package com.sicredi.vote.adapters.persistence;

import com.sicredi.vote.adapters.persistence.mapper.PautaMapper;
import com.sicredi.vote.adapters.persistence.repository.PautaJpaRepository;
import com.sicredi.vote.application.port.out.PautaRepository;
import com.sicredi.vote.domain.model.Pauta;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class PautaRepositoryAdapter implements PautaRepository {
  private final PautaJpaRepository jpa;

  public PautaRepositoryAdapter(PautaJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public Pauta salvar(Pauta pauta) {
    return PautaMapper.toDomain(jpa.save(PautaMapper.toEntity(pauta)));
  }

  @Override
  public Optional<Pauta> buscarPorId(UUID id) {
    return jpa.findById(id).map(PautaMapper::toDomain);
  }

  @Override
  public java.util.List<com.sicredi.vote.domain.model.Pauta> listarTodas() {
    return jpa.findAll().stream()
        .map(com.sicredi.vote.adapters.persistence.mapper.PautaMapper::toDomain)
        .toList();
  }
}
