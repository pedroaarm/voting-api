package com.sicredi.vote.adapters.web.telas;

import com.sicredi.vote.adapters.web.telas.model.*;
import com.sicredi.vote.config.CallbackProperties;
import com.sicredi.vote.domain.model.ResultadoVotacao;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class TelaFactory {

    private final CallbackProperties callback;
    private final MessageSource messages;

    public TelaFactory(CallbackProperties callback, MessageSource messages) {
        this.callback = callback;
        this.messages = messages;
    }

    private String msg(String key, Object... args) {
        return messages.getMessage(key, args, LocaleContextHolder.getLocale());
    }
    private String api(String path)  { return callback.baseUrl() + "/api/v1" + path; }
    private String tela(String path) { return callback.baseUrl() + "/api/v1/telas" + path; }

    public TelaSelecao menu() {
        return TelaSelecao.builder().tipo("SELECAO").titulo(msg("tela.menu.titulo"))
            .itens(List.of(
                ItemSelecao.builder().texto(msg("tela.menu.nova-pauta")).url(tela("/pautas/nova")).build(),
                ItemSelecao.builder().texto(msg("tela.menu.pautas")).url(tela("/pautas")).build()))
            .build();
    }

    public TelaSelecao listaPautas(java.util.List<com.sicredi.vote.domain.model.Pauta> pautas) {
        var itens = pautas.stream()
            .map(p -> ItemSelecao.builder().texto(p.getTitulo()).url(tela("/pautas/" + p.getId())).build())
            .toList();
        return TelaSelecao.builder().tipo("SELECAO").titulo(msg("tela.pautas.titulo")).itens(itens).build();
    }

    public TelaSelecao acoesPauta(java.util.UUID pautaId) {
        return TelaSelecao.builder().tipo("SELECAO").titulo(msg("tela.pauta.acoes.titulo"))
            .itens(List.of(
                ItemSelecao.builder().texto(msg("tela.pauta.abrir-sessao")).url(tela("/pautas/" + pautaId + "/sessao/nova")).build(),
                ItemSelecao.builder().texto(msg("tela.pauta.votar")).url(tela("/pautas/" + pautaId + "/voto")).build(),
                ItemSelecao.builder().texto(msg("tela.pauta.resultado")).url(tela("/pautas/" + pautaId + "/resultado")).build()))
            .build();
    }

    public TelaFormulario novaPauta() {
        return TelaFormulario.builder().tipo("FORMULARIO").titulo(msg("tela.nova-pauta.titulo"))
            .itens(List.of(
                ItemFormulario.builder().tipo("INPUT_TEXTO").id("titulo").titulo(msg("tela.nova-pauta.campo.titulo")).valor("").build(),
                ItemFormulario.builder().tipo("INPUT_TEXTO").id("descricao").titulo(msg("tela.nova-pauta.campo.descricao")).valor("").build()))
            .botaoOk(Botao.builder().texto(msg("tela.botao.confirmar")).url(api("/pautas")).body(Map.of()).build())
            .botaoCancelar(Botao.builder().texto(msg("tela.botao.cancelar")).url(tela("/menu")).build())
            .build();
    }

    public TelaFormulario abrirSessao(UUID pautaId) {
        return TelaFormulario.builder().tipo("FORMULARIO").titulo(msg("tela.abrir-sessao.titulo"))
            .itens(List.of(
                ItemFormulario.builder().tipo("INPUT_NUMERO").id("duracaoMinutos").titulo(msg("tela.abrir-sessao.campo.duracao")).valor("1").build()))
            .botaoOk(Botao.builder().texto(msg("tela.botao.confirmar")).url(api("/pautas/" + pautaId + "/sessoes")).body(Map.of()).build())
            .botaoCancelar(Botao.builder().texto(msg("tela.botao.cancelar")).url(tela("/menu")).build())
            .build();
    }

    public TelaFormulario votoIdentificacao(UUID pautaId) {
        return TelaFormulario.builder().tipo("FORMULARIO").titulo(msg("tela.voto.titulo"))
            .itens(List.of(
                ItemFormulario.builder().tipo("INPUT_TEXTO").id("associadoId").titulo(msg("tela.voto.campo.associado")).valor("").build(),
                ItemFormulario.builder().tipo("INPUT_TEXTO").id("cpf").titulo(msg("tela.voto.campo.cpf")).valor("").build()))
            .botaoOk(Botao.builder().texto(msg("tela.voto.botao.continuar")).url(tela("/pautas/" + pautaId + "/voto/opcoes")).body(Map.of()).build())
            .botaoCancelar(Botao.builder().texto(msg("tela.botao.cancelar")).url(tela("/menu")).build())
            .build();
    }

    public TelaSelecao votoOpcoes(UUID pautaId, String associadoId, String cpf) {
        String url = api("/pautas/" + pautaId + "/votos");
        return TelaSelecao.builder().tipo("SELECAO").titulo(msg("tela.voto.opcoes.titulo"))
            .itens(List.of(
                ItemSelecao.builder().texto(msg("tela.voto.opcoes.sim")).url(url)
                    .body(Map.of("associadoId", associadoId, "cpf", cpf, "opcao", "SIM")).build(),
                ItemSelecao.builder().texto(msg("tela.voto.opcoes.nao")).url(url)
                    .body(Map.of("associadoId", associadoId, "cpf", cpf, "opcao", "NAO")).build()))
            .build();
    }

    public TelaFormulario resultado(ResultadoVotacao r) {
        return TelaFormulario.builder().tipo("FORMULARIO").titulo(msg("tela.resultado.titulo"))
            .itens(List.of(
                ItemFormulario.builder().tipo("TEXTO").texto(msg("tela.resultado.status", r.getStatus())).build(),
                ItemFormulario.builder().tipo("TEXTO").texto(msg("tela.resultado.sim", r.getTotalSim())).build(),
                ItemFormulario.builder().tipo("TEXTO").texto(msg("tela.resultado.nao", r.getTotalNao())).build()))
            .botaoOk(Botao.builder().texto(msg("tela.botao.ok")).url(tela("/menu")).build())
            .build();
    }
}
