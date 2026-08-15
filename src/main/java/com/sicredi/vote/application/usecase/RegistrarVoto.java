package com.sicredi.vote.application.usecase;

import com.sicredi.vote.application.exception.*;
import com.sicredi.vote.application.port.out.*;
import com.sicredi.vote.domain.model.OpcaoVoto;
import com.sicredi.vote.domain.model.Sessao;
import com.sicredi.vote.domain.model.Voto;
import java.time.Clock;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RegistrarVoto {

  private final SessaoRepository sessoes;
  private final VotoRepository votos;
  private final VerificadorElegibilidade elegibilidade;
  private final Clock clock;

  public RegistrarVoto(
      SessaoRepository sessoes,
      VotoRepository votos,
      VerificadorElegibilidade elegibilidade,
      Clock clock) {
    this.sessoes = sessoes;
    this.votos = votos;
    this.elegibilidade = elegibilidade;
    this.clock = clock;
  }

  public Voto executar(UUID pautaId, String associadoId, String cpf, OpcaoVoto opcao) {
    // 1) janela da sessão — fora de transação
    Sessao sessao =
        sessoes
            .buscarPorPauta(pautaId)
            .filter(s -> s.estaAberta(clock.instant()))
            .orElseThrow(() -> new SessaoFechadaException(pautaId));

    // 2) elegibilidade — chamada externa FORA da transação (Plano 3 troca a impl)
    if (elegibilidade.verificar(cpf) == Elegibilidade.UNABLE_TO_VOTE) {
      throw new AssociadoInelegivelException(cpf);
    }

    // 3) persistência — unicidade garante 1 voto por associado/pauta
    Voto voto =
        Voto.builder()
            .id(UUID.randomUUID())
            .pautaId(sessao.getPautaId())
            .associadoId(associadoId)
            .opcao(opcao)
            .criadoEm(clock.instant())
            .build();
    return votos.salvar(voto);
  }
}
