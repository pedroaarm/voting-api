package com.sicredi.vote.application.usecase;

import com.sicredi.vote.application.exception.*;
import com.sicredi.vote.application.fake.*;
import com.sicredi.vote.application.port.out.Elegibilidade;
import com.sicredi.vote.domain.model.OpcaoVoto;
import com.sicredi.vote.domain.model.Sessao;
import org.junit.jupiter.api.Test;
import java.time.*;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class RegistrarVotoTest {

    private final Instant agora = Instant.parse("2026-08-12T10:00:00Z");
    private final Clock clock = Clock.fixed(agora, ZoneOffset.UTC);
    private final FakeSessaoRepository sessoes = new FakeSessaoRepository();
    private final FakeVotoRepository votos = new FakeVotoRepository();
    private final FakeVerificadorElegibilidade elegibilidade = new FakeVerificadorElegibilidade();
    private final RegistrarVoto useCase = new RegistrarVoto(sessoes, votos, elegibilidade, clock);

    private final UUID pauta = UUID.randomUUID();

    private void sessaoAberta() { sessoes.salvar(Sessao.abrir(pauta, agora, 1)); }

    @Test
    void registraVotoQuandoSessaoAbertaEElegivel() {
        sessaoAberta();
        var voto = useCase.executar(pauta, "assoc-1", "19839091069", OpcaoVoto.SIM);
        assertThat(voto.getId()).isNotNull();
        assertThat(voto.getOpcao()).isEqualTo(OpcaoVoto.SIM);
        assertThat(votos.dados).hasSize(1);
    }

    @Test
    void rejeitaQuandoSessaoFechada() {
        sessoes.salvar(Sessao.abrir(pauta, agora.minus(Duration.ofMinutes(5)), 1)); // já fechou
        assertThatThrownBy(() -> useCase.executar(pauta, "assoc-1", "cpf", OpcaoVoto.SIM))
            .isInstanceOf(SessaoFechadaException.class);
    }

    @Test
    void rejeitaQuandoNaoHaSessao() {
        assertThatThrownBy(() -> useCase.executar(pauta, "assoc-1", "cpf", OpcaoVoto.SIM))
            .isInstanceOf(SessaoFechadaException.class);
    }

    @Test
    void rejeitaQuandoInelegivel() {
        sessaoAberta();
        elegibilidade.resposta = Elegibilidade.UNABLE_TO_VOTE;
        assertThatThrownBy(() -> useCase.executar(pauta, "assoc-1", "cpf", OpcaoVoto.SIM))
            .isInstanceOf(AssociadoInelegivelException.class);
        assertThat(votos.dados).isEmpty();
    }

    @Test
    void propagaIndisponibilidadeDoServicoDeCpf() {
        sessaoAberta();
        elegibilidade.indisponivel = true;
        assertThatThrownBy(() -> useCase.executar(pauta, "assoc-1", "cpf", OpcaoVoto.SIM))
            .isInstanceOf(ElegibilidadeIndisponivelException.class);
        assertThat(votos.dados).isEmpty();
    }

    @Test
    void rejeitaVotoDuplicado() {
        sessaoAberta();
        useCase.executar(pauta, "assoc-1", "cpf", OpcaoVoto.SIM);
        assertThatThrownBy(() -> useCase.executar(pauta, "assoc-1", "cpf", OpcaoVoto.NAO))
            .isInstanceOf(VotoDuplicadoException.class);
    }
}
