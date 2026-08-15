package com.sicredi.vote.adapters.persistence.repository;

import com.sicredi.vote.adapters.persistence.ContagemOpcao;
import com.sicredi.vote.adapters.persistence.entity.VotoJpaEntity;
import com.sicredi.vote.domain.model.OpcaoVoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface VotoJpaRepository extends JpaRepository<VotoJpaEntity, UUID> {
    long countByPautaIdAndOpcao(UUID pautaId, OpcaoVoto opcao);

    @Query("select v.opcao as opcao, count(v) as total from VotoJpaEntity v " +
           "where v.pautaId = :pautaId group by v.opcao")
    java.util.List<ContagemOpcao> contarPorOpcao(@Param("pautaId") UUID pautaId);
}
