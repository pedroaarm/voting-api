package com.sicredi.vote.application.usecase;

import static org.assertj.core.api.Assertions.*;

import com.sicredi.vote.application.exception.PautaNaoEncontradaException;
import com.sicredi.vote.application.exception.SessaoEmAndamentoException;
import com.sicredi.vote.application.fake.*;
import com.sicredi.vote.domain.model.*;
import java.time.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ConsultarResultadoTest {

  private final Instant agora = Instant.parse("2026-08-12T10:00:00Z");
  private final Clock clock = Clock.fixed(agora, ZoneOffset.UTC);
  private final FakePautaRepository pautas = new FakePautaRepository();
  private final FakeSessaoRepository sessoes = new FakeSessaoRepository();
  private final FakeVotoRepository votos = new FakeVotoRepository();
  private final ConsultarResultado useCase = new ConsultarResultado(pautas, sessoes, votos, clock);

  private UUID pautaSalva() {
    var p = Pauta.builder().id(UUID.randomUUID()).titulo("t").criadaEm(agora).build();
    pautas.salvar(p);
    return p.getId();
  }

  @Test
  void falhaQuandoPautaNaoExiste() {
    assertThatThrownBy(() -> useCase.executar(UUID.randomUUID()))
        .isInstanceOf(PautaNaoEncontradaException.class);
  }

  @Test
  void falhaQuandoSessaoAindaEmAndamento() {
    UUID pauta = pautaSalva();
    sessoes.salvar(Sessao.abrir(pauta, agora, 1)); // aberta agora
    assertThatThrownBy(() -> useCase.executar(pauta))
        .isInstanceOf(SessaoEmAndamentoException.class);
  }

  @Test
  void apuraAposFechamento() {
    UUID pauta = pautaSalva();
    sessoes.salvar(Sessao.abrir(pauta, agora.minus(Duration.ofMinutes(5)), 1)); // já fechou
    votos.salvar(
        Voto.builder()
            .id(UUID.randomUUID())
            .pautaId(pauta)
            .associadoId("a")
            .opcao(OpcaoVoto.SIM)
            .criadoEm(agora)
            .build());
    votos.salvar(
        Voto.builder()
            .id(UUID.randomUUID())
            .pautaId(pauta)
            .associadoId("b")
            .opcao(OpcaoVoto.SIM)
            .criadoEm(agora)
            .build());
    votos.salvar(
        Voto.builder()
            .id(UUID.randomUUID())
            .pautaId(pauta)
            .associadoId("c")
            .opcao(OpcaoVoto.NAO)
            .criadoEm(agora)
            .build());

    var r = useCase.executar(pauta);
    assertThat(r.getTotalSim()).isEqualTo(2);
    assertThat(r.getTotalNao()).isEqualTo(1);
    assertThat(r.getStatus()).isEqualTo(StatusResultado.APROVADA);
  }
}
