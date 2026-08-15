package com.sicredi.vote.adapters.persistence.repository;

import com.sicredi.vote.adapters.persistence.entity.SessaoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface SessaoJpaRepository extends JpaRepository<SessaoJpaEntity, UUID> {
    Optional<SessaoJpaEntity> findByPautaId(UUID pautaId);
    boolean existsByPautaId(UUID pautaId);
}
