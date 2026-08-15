package com.sicredi.vote.adapters.web.telas;

import com.sicredi.vote.config.CallbackProperties;
import com.sicredi.vote.domain.model.Pauta;
import com.sicredi.vote.domain.model.ResultadoVotacao;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.ResourceBundleMessageSource;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TelaFactoryTest {

    private TelaFactory factory() {
        var ms = new ResourceBundleMessageSource();
        ms.setBasename("messages");
        ms.setDefaultEncoding("UTF-8");
        return new TelaFactory(new CallbackProperties("http://exemplo.com"), ms);
    }

    @Test
    void novaPautaApontaParaEndpointRealComBaseUrl() {
        var tela = factory().novaPauta();
        assertThat(tela.tipo()).isEqualTo("FORMULARIO");
        assertThat(tela.botaoOk().url()).isEqualTo("http://exemplo.com/api/v1/pautas");
        assertThat(tela.itens()).anyMatch(i -> "titulo".equals(i.id()));
    }

    @Test
    void votoOpcoesEmbutePayloadCompletoEmCadaOpcao() {
        UUID pauta = UUID.randomUUID();
        var tela = factory().votoOpcoes(pauta, "a1", "19839091069");
        assertThat(tela.tipo()).isEqualTo("SELECAO");
        assertThat(tela.itens()).hasSize(2);
        var sim = tela.itens().get(0);
        assertThat(sim.url()).isEqualTo("http://exemplo.com/api/v1/pautas/" + pauta + "/votos");
        assertThat(sim.body()).containsEntry("associadoId", "a1")
            .containsEntry("cpf", "19839091069").containsEntry("opcao", "SIM");
    }

    @Test
    void menuListaOpcoesDeNavegacao() {
        var tela = factory().menu();
        assertThat(tela.tipo()).isEqualTo("SELECAO");
        assertThat(tela.itens()).isNotEmpty();
        assertThat(tela.itens().get(0).url()).startsWith("http://exemplo.com/api/v1/telas");
    }

    @Test
    void menuNaoReferenciaEndpointsInexistentes() {
        var tela = factory().menu();
        assertThat(tela.itens()).allMatch(i ->
            i.url().equals("http://exemplo.com/api/v1/telas/pautas/nova")
                || i.url().equals("http://exemplo.com/api/v1/telas/pautas"));
    }

    @Test
    void acoesPautaApontaApenasParaEndpointsReais() {
        UUID pautaId = UUID.randomUUID();
        var tela = factory().acoesPauta(pautaId);
        assertThat(tela.tipo()).isEqualTo("SELECAO");
        assertThat(tela.itens()).hasSize(3);
        assertThat(tela.itens().get(0).url()).isEqualTo("http://exemplo.com/api/v1/telas/pautas/" + pautaId + "/sessao/nova");
        assertThat(tela.itens().get(1).url()).isEqualTo("http://exemplo.com/api/v1/telas/pautas/" + pautaId + "/voto");
        assertThat(tela.itens().get(2).url()).isEqualTo("http://exemplo.com/api/v1/telas/pautas/" + pautaId + "/resultado");
    }

    @Test
    void listaPautasGeraUmItemPorPautaApontandoParaAcoesDaPauta() {
        UUID pautaId = UUID.randomUUID();
        var pauta = Pauta.builder().id(pautaId).titulo("Reforma do estatuto").descricao("desc").build();
        var tela = factory().listaPautas(List.of(pauta));
        assertThat(tela.tipo()).isEqualTo("SELECAO");
        assertThat(tela.itens()).hasSize(1);
        assertThat(tela.itens().get(0).url()).isEqualTo("http://exemplo.com/api/v1/telas/pautas/" + pautaId);
        assertThat(tela.itens().get(0).texto()).isEqualTo("Reforma do estatuto");
    }
}
