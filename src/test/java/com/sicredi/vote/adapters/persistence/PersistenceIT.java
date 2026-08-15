package com.sicredi.vote.adapters.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.sicredi.vote.application.port.out.*;
import com.sicredi.vote.domain.model.*;
import com.sicredi.vote.support.AbstractPostgresIT;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class PersistenceIT extends AbstractPostgresIT {

  @Autowired PautaRepository pautas;
  @Autowired SessaoRepository sessoes;
  @Autowired VotoRepository votos;

  @Test
  void salvaERecuperaPautaSessaoEVoto() {
    Instant agora = Instant.parse("2026-08-12T10:00:00Z");
    Pauta pauta =
        pautas.salvar(
            Pauta.builder()
                .id(UUID.randomUUID())
                .titulo("Pauta X")
                .descricao("desc")
                .criadaEm(agora)
                .build());

    assertThat(pautas.buscarPorId(pauta.getId())).isPresent();

    sessoes.salvar(Sessao.abrir(pauta.getId(), agora, 1));
    assertThat(sessoes.existePorPauta(pauta.getId())).isTrue();

    votos.salvar(
        Voto.builder()
            .id(UUID.randomUUID())
            .pautaId(pauta.getId())
            .associadoId("assoc-1")
            .opcao(OpcaoVoto.SIM)
            .criadoEm(agora)
            .build());
    assertThat(votos.contar(pauta.getId(), OpcaoVoto.SIM)).isEqualTo(1);
    assertThat(votos.contar(pauta.getId(), OpcaoVoto.NAO)).isZero();
  }
}
