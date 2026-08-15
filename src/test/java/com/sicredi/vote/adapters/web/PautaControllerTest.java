package com.sicredi.vote.adapters.web;

import com.sicredi.vote.application.exception.PautaNaoEncontradaException;
import com.sicredi.vote.application.usecase.AbrirSessao;
import com.sicredi.vote.application.usecase.CadastrarPauta;
import com.sicredi.vote.domain.model.Pauta;
import com.sicredi.vote.domain.model.Sessao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PautaController.class)
@Import(ProblemDetailAdvice.class)
class PautaControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean CadastrarPauta cadastrarPauta;
    @MockitoBean AbrirSessao abrirSessao;
    @MockitoBean VoteMetrics metrics;

    @Test
    void cadastraPautaRetorna201ComLocation() throws Exception {
        UUID id = UUID.randomUUID();
        when(cadastrarPauta.executar(any(), any()))
            .thenReturn(Pauta.builder().id(id).titulo("Reforma").descricao("d").criadaEm(Instant.parse("2026-08-13T10:00:00Z")).build());

        mvc.perform(post("/api/v1/pautas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"Reforma\",\"descricao\":\"d\"}"))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/v1/pautas/" + id))
            .andExpect(jsonPath("$.id").value(id.toString()))
            .andExpect(jsonPath("$.titulo").value("Reforma"));
    }

    @Test
    void tituloEmBrancoRetorna400() throws Exception {
        mvc.perform(post("/api/v1/pautas").contentType(MediaType.APPLICATION_JSON)
                .content("{\"titulo\":\"\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("urn:vote:validacao"));
    }

    @Test
    void abreSessaoRetorna201ComLocation() throws Exception {
        UUID pauta = UUID.randomUUID();
        UUID sessao = UUID.randomUUID();
        Instant t = Instant.parse("2026-08-13T10:00:00Z");
        when(abrirSessao.executar(eq(pauta), any()))
            .thenReturn(Sessao.builder().id(sessao).pautaId(pauta).abertura(t).fechamento(t.plusSeconds(60)).build());

        mvc.perform(post("/api/v1/pautas/" + pauta + "/sessoes").contentType(MediaType.APPLICATION_JSON)
                .content("{\"duracaoMinutos\":1}"))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/api/v1/pautas/" + pauta + "/sessoes/" + sessao))
            .andExpect(jsonPath("$.pautaId").value(pauta.toString()));
    }

    @Test
    void abreSessaoEmPautaInexistenteRetorna404() throws Exception {
        UUID pauta = UUID.randomUUID();
        when(abrirSessao.executar(eq(pauta), any())).thenThrow(new PautaNaoEncontradaException(pauta));

        mvc.perform(post("/api/v1/pautas/" + pauta + "/sessoes").contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.type").value("urn:vote:pauta-nao-encontrada"));
    }
}
