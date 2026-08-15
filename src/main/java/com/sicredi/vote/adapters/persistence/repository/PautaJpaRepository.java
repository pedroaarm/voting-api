package com.sicredi.vote.adapters.persistence.repository;

import com.sicredi.vote.adapters.persistence.entity.PautaJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PautaJpaRepository extends JpaRepository<PautaJpaEntity, UUID> {}
