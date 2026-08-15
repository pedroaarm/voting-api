package com.sicredi.vote.application.usecase;

import com.sicredi.vote.application.port.out.PautaRepository;
import com.sicredi.vote.domain.model.Pauta;
import org.springframework.stereotype.Service;
import java.time.Clock;
import java.util.UUID;

@Service
public class CadastrarPauta {

    private final PautaRepository repository;
    private final Clock clock;

    public CadastrarPauta(PautaRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public Pauta executar(String titulo, String descricao) {
        Pauta pauta = Pauta.builder()
            .id(UUID.randomUUID())
            .titulo(titulo)
            .descricao(descricao)
            .criadaEm(clock.instant())
            .build();
        return repository.salvar(pauta);
    }
}
