package com.sicredi.vote.adapters.persistence.repository;

import com.sicredi.vote.adapters.persistence.entity.VotoJpaEntity;
import com.sicredi.vote.domain.model.OpcaoVoto;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotoJpaRepository extends JpaRepository<VotoJpaEntity, UUID> {
  long countByPautaIdAndOpcao(UUID pautaId, OpcaoVoto opcao);
}
