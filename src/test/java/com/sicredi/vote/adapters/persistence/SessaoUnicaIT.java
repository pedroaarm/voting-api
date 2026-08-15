package com.sicredi.vote.adapters.persistence;

import com.sicredi.vote.application.exception.SessaoJaAbertaException;
import com.sicredi.vote.application.port.out.PautaRepository;
import com.sicredi.vote.application.port.out.SessaoRepository;
import com.sicredi.vote.domain.model.*;
import com.sicredi.vote.support.AbstractPostgresIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SessaoUnicaIT extends AbstractPostgresIT {

    @Autowired PautaRepository pautas;
    @Autowired SessaoRepository sessoes;

    @Test
    void segundaSessaoDaMesmaPautaLancaSessaoJaAberta() {
        Instant agora = Instant.now();
        UUID pauta = UUID.randomUUID();
        pautas.salvar(Pauta.builder().id(pauta).titulo("p").criadaEm(agora).build());

        sessoes.salvar(Sessao.abrir(pauta, agora, 1));

        assertThatThrownBy(() -> sessoes.salvar(Sessao.abrir(pauta, agora, 1)))
            .isInstanceOf(SessaoJaAbertaException.class);
    }
}
