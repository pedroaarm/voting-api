package com.sicredi.vote.adapters.persistence.repository;

import com.sicredi.vote.adapters.persistence.entity.PautaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface PautaJpaRepository extends JpaRepository<PautaJpaEntity, UUID> {}
