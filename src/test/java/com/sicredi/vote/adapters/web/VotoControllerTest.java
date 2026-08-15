package com.sicredi.vote.adapters.web;

import com.sicredi.vote.application.exception.*;
import com.sicredi.vote.application.usecase.RegistrarVoto;
import com.sicredi.vote.domain.model.OpcaoVoto;
import com.sicredi.vote.domain.model.Voto;
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

@WebMvcTest(VotoController.class)
@Import(ProblemDetailAdvice.class)
class VotoControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean RegistrarVoto registrarVoto;
    @MockitoBean VoteMetrics metrics;

    private static final String BODY = "{\"associadoId\":\"a1\",\"cpf\":\"19839091069\",\"opcao\":\"SIM\"}";

    @Test
    void registraVotoRetorna201() throws Exception {
        UUID pauta = UUID.randomUUID();
        when(registrarVoto.executar(eq(pauta), eq("a1"), eq("19839091069"), eq(OpcaoVoto.SIM)))
            .thenReturn(Voto.builder().id(UUID.randomUUID()).pautaId(pauta).associadoId("a1").opcao(OpcaoVoto.SIM).criadoEm(Instant.now()).build());

        mvc.perform(post("/api/v1/pautas/" + pauta + "/votos").contentType(MediaType.APPLICATION_JSON).content(BODY))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.opcao").value("SIM"));
    }

    @Test
    void votoDuplicadoRetorna409() throws Exception {
        UUID pauta = UUID.randomUUID();
        when(registrarVoto.executar(any(), any(), any(), any())).thenThrow(new VotoDuplicadoException("a1"));
        mvc.perform(post("/api/v1/pautas/" + pauta + "/votos").contentType(MediaType.APPLICATION_JSON).content(BODY))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.type").value("urn:vote:voto-duplicado"));
    }

    @Test
    void sessaoFechadaRetorna422() throws Exception {
        UUID pauta = UUID.randomUUID();
        when(registrarVoto.executar(any(), any(), any(), any())).thenThrow(new SessaoFechadaException(pauta));
        mvc.perform(post("/api/v1/pautas/" + pauta + "/votos").contentType(MediaType.APPLICATION_JSON).content(BODY))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(jsonPath("$.type").value("urn:vote:sessao-fechada"));
    }

    @Test
    void elegibilidadeIndisponivelRetorna503() throws Exception {
        UUID pauta = UUID.randomUUID();
        when(registrarVoto.executar(any(), any(), any(), any())).thenThrow(new ElegibilidadeIndisponivelException("fora"));
        mvc.perform(post("/api/v1/pautas/" + pauta + "/votos").contentType(MediaType.APPLICATION_JSON).content(BODY))
            .andExpect(status().isServiceUnavailable())
            .andExpect(jsonPath("$.type").value("urn:vote:elegibilidade-indisponivel"));
    }

    @Test
    void opcaoInvalidaRetorna400() throws Exception {
        UUID pauta = UUID.randomUUID();
        mvc.perform(post("/api/v1/pautas/" + pauta + "/votos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"associadoId\":\"a1\",\"cpf\":\"1\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("urn:vote:validacao"));
    }

    @Test
    void opcaoComLiteralEnumInvalidoRetorna400() throws Exception {
        UUID pauta = UUID.randomUUID();
        mvc.perform(post("/api/v1/pautas/" + pauta + "/votos").contentType(MediaType.APPLICATION_JSON)
                .content("{\"associadoId\":\"a1\",\"cpf\":\"1\",\"opcao\":\"TALVEZ\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.type").value("urn:vote:validacao"));
    }
}
