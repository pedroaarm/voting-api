package com.sicredi.vote.adapters.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.sicredi.vote.adapters.persistence.entity.VotoJpaEntity;
import com.sicredi.vote.adapters.persistence.repository.VotoJpaRepository;
import com.sicredi.vote.application.port.out.PautaRepository;
import com.sicredi.vote.domain.model.OpcaoVoto;
import com.sicredi.vote.domain.model.Pauta;
import com.sicredi.vote.support.AbstractPostgresIT;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ApuracaoIT extends AbstractPostgresIT {

  @Autowired PautaRepository pautas;
  @Autowired VotoJpaRepository votoJpa;

  @Test
  void contaAgregadoPorOpcao() {
    Instant agora = Instant.now();
    UUID pauta = UUID.randomUUID();
    pautas.salvar(Pauta.builder().id(pauta).titulo("p").criadaEm(agora).build());

    for (int i = 0; i < 7; i++)
      votoJpa.save(new VotoJpaEntity(UUID.randomUUID(), pauta, "sim-" + i, OpcaoVoto.SIM, agora));
    for (int i = 0; i < 3; i++)
      votoJpa.save(new VotoJpaEntity(UUID.randomUUID(), pauta, "nao-" + i, OpcaoVoto.NAO, agora));

    assertThat(votoJpa.countByPautaIdAndOpcao(pauta, OpcaoVoto.SIM)).isEqualTo(7);
    assertThat(votoJpa.countByPautaIdAndOpcao(pauta, OpcaoVoto.NAO)).isEqualTo(3);
    assertThat(votoJpa.contarPorOpcao(pauta)).hasSize(2);
  }
}
