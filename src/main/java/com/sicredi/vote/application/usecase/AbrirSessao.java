package com.sicredi.vote.application.usecase;

import com.sicredi.vote.application.exception.PautaNaoEncontradaException;
import com.sicredi.vote.application.exception.SessaoJaAbertaException;
import com.sicredi.vote.application.port.out.PautaRepository;
import com.sicredi.vote.application.port.out.SessaoRepository;
import com.sicredi.vote.config.SessaoProperties;
import com.sicredi.vote.domain.model.Sessao;
import org.springframework.stereotype.Service;
import java.time.Clock;
import java.util.UUID;

@Service
public class AbrirSessao {

    private final PautaRepository pautas;
    private final SessaoRepository sessoes;
    private final SessaoProperties props;
    private final Clock clock;

    public AbrirSessao(PautaRepository pautas, SessaoRepository sessoes,
                       SessaoProperties props, Clock clock) {
        this.pautas = pautas;
        this.sessoes = sessoes;
        this.props = props;
        this.clock = clock;
    }

    public Sessao executar(UUID pautaId, Integer duracaoMinutos) {
        if (pautas.buscarPorId(pautaId).isEmpty()) {
            throw new PautaNaoEncontradaException(pautaId);
        }
        if (sessoes.existePorPauta(pautaId)) {
            throw new SessaoJaAbertaException(pautaId);
        }
        int duracao = duracaoMinutos != null ? duracaoMinutos : props.duracaoDefaultMinutos();
        Sessao sessao = Sessao.abrir(pautaId, clock.instant(), duracao);
        return sessoes.salvar(sessao);
    }
}
