package com.sicredi.vote.application.usecase;

import static org.assertj.core.api.Assertions.*;

import com.sicredi.vote.application.exception.PautaNaoEncontradaException;
import com.sicredi.vote.application.exception.SessaoJaAbertaException;
import com.sicredi.vote.application.fake.FakePautaRepository;
import com.sicredi.vote.application.fake.FakeSessaoRepository;
import com.sicredi.vote.config.SessaoProperties;
import com.sicredi.vote.domain.model.Pauta;
import java.time.*;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AbrirSessaoTest {

  private final Instant agora = Instant.parse("2026-08-12T10:00:00Z");
  private final Clock clock = Clock.fixed(agora, ZoneOffset.UTC);
  private final FakePautaRepository pautas = new FakePautaRepository();
  private final FakeSessaoRepository sessoes = new FakeSessaoRepository();
  private final SessaoProperties props = new SessaoProperties(1);
  private final AbrirSessao useCase = new AbrirSessao(pautas, sessoes, props, clock);

  private UUID pautaSalva() {
    var p = Pauta.builder().id(UUID.randomUUID()).titulo("t").criadaEm(agora).build();
    pautas.salvar(p);
    return p.getId();
  }

  @Test
  void abreComDuracaoDefaultQuandoNaoInformada() {
    UUID pauta = pautaSalva();
    var s = useCase.executar(pauta, null);
    assertThat(s.getAbertura()).isEqualTo(agora);
    assertThat(s.getFechamento()).isEqualTo(agora.plus(Duration.ofMinutes(1)));
  }

  @Test
  void abreComDuracaoInformada() {
    UUID pauta = pautaSalva();
    var s = useCase.executar(pauta, 5);
    assertThat(s.getFechamento()).isEqualTo(agora.plus(Duration.ofMinutes(5)));
  }

  @Test
  void falhaQuandoPautaNaoExiste() {
    assertThatThrownBy(() -> useCase.executar(UUID.randomUUID(), null))
        .isInstanceOf(PautaNaoEncontradaException.class);
  }

  @Test
  void falhaQuandoJaHaSessao() {
    UUID pauta = pautaSalva();
    useCase.executar(pauta, null);
    assertThatThrownBy(() -> useCase.executar(pauta, null))
        .isInstanceOf(SessaoJaAbertaException.class);
  }
}
