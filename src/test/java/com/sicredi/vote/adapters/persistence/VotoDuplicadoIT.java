package com.sicredi.vote.adapters.persistence;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sicredi.vote.application.exception.VotoDuplicadoException;
import com.sicredi.vote.application.port.out.PautaRepository;
import com.sicredi.vote.application.port.out.VotoRepository;
import com.sicredi.vote.domain.model.*;
import com.sicredi.vote.support.AbstractPostgresIT;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class VotoDuplicadoIT extends AbstractPostgresIT {

  @Autowired PautaRepository pautas;
  @Autowired VotoRepository votos;

  @Test
  void segundoVotoDoMesmoAssociadoLancaVotoDuplicado() {
    Instant agora = Instant.now();
    UUID pauta = UUID.randomUUID();
    pautas.salvar(Pauta.builder().id(pauta).titulo("p").criadaEm(agora).build());

    votos.salvar(
        Voto.builder()
            .id(UUID.randomUUID())
            .pautaId(pauta)
            .associadoId("assoc-1")
            .opcao(OpcaoVoto.SIM)
            .criadoEm(agora)
            .build());

    assertThatThrownBy(
            () ->
                votos.salvar(
                    Voto.builder()
                        .id(UUID.randomUUID())
                        .pautaId(pauta)
                        .associadoId("assoc-1")
                        .opcao(OpcaoVoto.NAO)
                        .criadoEm(agora)
                        .build()))
        .isInstanceOf(VotoDuplicadoException.class);
  }
}
