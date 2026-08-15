package com.sicredi.vote.adapters.persistence;

import com.sicredi.vote.adapters.persistence.mapper.PautaMapper;
import com.sicredi.vote.adapters.persistence.repository.PautaJpaRepository;
import com.sicredi.vote.application.port.out.PautaRepository;
import com.sicredi.vote.config.CacheConfig;
import com.sicredi.vote.domain.model.Pauta;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@Repository
public class PautaRepositoryAdapter implements PautaRepository {
  private final PautaJpaRepository jpa;

  public PautaRepositoryAdapter(PautaJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  @CacheEvict(cacheNames = CacheConfig.CACHE_PAUTAS_LISTA, allEntries = true)
  public Pauta salvar(Pauta pauta) {
    return PautaMapper.toDomain(jpa.save(PautaMapper.toEntity(pauta)));
  }

  @Override
  @Cacheable(cacheNames = CacheConfig.CACHE_PAUTA, key = "#id", unless = "#result == null")
  public Optional<Pauta> buscarPorId(UUID id) {
    return jpa.findById(id).map(PautaMapper::toDomain);
  }

  @Override
  @Cacheable(cacheNames = CacheConfig.CACHE_PAUTAS_LISTA, key = "'todas'")
  public List<Pauta> listarTodas() {
    // ArrayList (mutavel) para round-trip no cache; a lista imutavel de toList() nao e
    // reconstruivel pelo Jackson ao ler de volta do Redis.
    return jpa.findAll().stream()
        .map(PautaMapper::toDomain)
        .collect(Collectors.toCollection(ArrayList::new));
  }
}
