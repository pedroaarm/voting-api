package com.sicredi.vote.application.usecase;

import com.sicredi.vote.application.fake.FakePautaRepository;
import org.junit.jupiter.api.Test;
import java.time.*;
import static org.assertj.core.api.Assertions.assertThat;

class CadastrarPautaTest {

    private final Clock clock = Clock.fixed(Instant.parse("2026-08-12T10:00:00Z"), ZoneOffset.UTC);
    private final FakePautaRepository repo = new FakePautaRepository();
    private final CadastrarPauta useCase = new CadastrarPauta(repo, clock);

    @Test
    void cadastraPautaComIdECriadaEm() {
        var pauta = useCase.executar("Reforma do estatuto", "Detalhes...");
        assertThat(pauta.getId()).isNotNull();
        assertThat(pauta.getTitulo()).isEqualTo("Reforma do estatuto");
        assertThat(pauta.getCriadaEm()).isEqualTo(Instant.parse("2026-08-12T10:00:00Z"));
        assertThat(repo.dados).containsKey(pauta.getId());
    }
}
