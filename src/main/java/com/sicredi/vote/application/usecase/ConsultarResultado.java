package com.sicredi.vote.application.usecase;

import com.sicredi.vote.application.exception.PautaNaoEncontradaException;
import com.sicredi.vote.application.exception.SessaoEmAndamentoException;
import com.sicredi.vote.application.port.out.PautaRepository;
import com.sicredi.vote.application.port.out.SessaoRepository;
import com.sicredi.vote.application.port.out.VotoRepository;
import com.sicredi.vote.domain.model.OpcaoVoto;
import com.sicredi.vote.domain.model.ResultadoVotacao;
import com.sicredi.vote.domain.model.Sessao;
import org.springframework.stereotype.Service;
import java.time.Clock;
import java.util.UUID;

@Service
public class ConsultarResultado {

    private final PautaRepository pautas;
    private final SessaoRepository sessoes;
    private final VotoRepository votos;
    private final Clock clock;

    public ConsultarResultado(PautaRepository pautas, SessaoRepository sessoes,
                              VotoRepository votos, Clock clock) {
        this.pautas = pautas;
        this.sessoes = sessoes;
        this.votos = votos;
        this.clock = clock;
    }

    public ResultadoVotacao executar(UUID pautaId) {
        if (pautas.buscarPorId(pautaId).isEmpty()) {
            throw new PautaNaoEncontradaException(pautaId);
        }
        Sessao sessao = sessoes.buscarPorPauta(pautaId)
            .filter(s -> s.estaEncerrada(clock.instant()))
            .orElseThrow(() -> new SessaoEmAndamentoException(pautaId));

        long sim = votos.contar(sessao.getPautaId(), OpcaoVoto.SIM);
        long nao = votos.contar(sessao.getPautaId(), OpcaoVoto.NAO);
        return ResultadoVotacao.apurar(pautaId, sim, nao);
    }
}
