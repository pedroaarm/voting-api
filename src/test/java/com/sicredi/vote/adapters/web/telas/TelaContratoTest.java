package com.sicredi.vote.adapters.web.telas;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sicredi.vote.adapters.web.ProblemDetailAdvice;
import com.sicredi.vote.adapters.web.VoteMetrics;
import com.sicredi.vote.application.usecase.ConsultarResultado;
import com.sicredi.vote.application.usecase.ListarPautas;
import com.sicredi.vote.config.CallbackProperties;
import com.sicredi.vote.domain.model.Pauta;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TelaController.class)
@Import({TelaFactory.class, ProblemDetailAdvice.class, TelaContratoTest.TestConfig.class})
class TelaContratoTest {

  @TestConfiguration
  static class TestConfig {
    @Bean
    CallbackProperties callbackProperties() {
      return new CallbackProperties("http://localhost:8080");
    }
  }

  @Autowired MockMvc mvc;
  @MockitoBean ConsultarResultado consultarResultado;
  @MockitoBean ListarPautas listarPautas;
  @MockitoBean VoteMetrics metrics;

  @Test
  void telaNovaPautaTemFormatoFormulario() throws Exception {
    mvc.perform(get("/api/v1/telas/pautas/nova"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tipo").value("FORMULARIO"))
        .andExpect(jsonPath("$.itens[0].id").exists())
        .andExpect(jsonPath("$.botaoOk.url").value("http://localhost:8080/api/v1/pautas"));
  }

  @Test
  void telaPautasListaApontaParaEndpointsReais() throws Exception {
    UUID pautaId = UUID.randomUUID();
    when(listarPautas.executar())
        .thenReturn(
            List.of(
                Pauta.builder()
                    .id(pautaId)
                    .titulo("Reforma do estatuto")
                    .descricao("desc")
                    .build()));

    mvc.perform(get("/api/v1/telas/pautas"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tipo").value("SELECAO"))
        .andExpect(
            jsonPath("$.itens[0].url")
                .value("http://localhost:8080/api/v1/telas/pautas/" + pautaId));
  }

  @Test
  void telaAcoesPautaRetornaSelecaoComEndpointsReais() throws Exception {
    UUID pautaId = UUID.randomUUID();

    mvc.perform(get("/api/v1/telas/pautas/{id}", pautaId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tipo").value("SELECAO"))
        .andExpect(jsonPath("$.itens.length()").value(3))
        .andExpect(
            jsonPath("$.itens[0].url")
                .value("http://localhost:8080/api/v1/telas/pautas/" + pautaId + "/sessao/nova"))
        .andExpect(
            jsonPath("$.itens[1].url")
                .value("http://localhost:8080/api/v1/telas/pautas/" + pautaId + "/voto"))
        .andExpect(
            jsonPath("$.itens[2].url")
                .value("http://localhost:8080/api/v1/telas/pautas/" + pautaId + "/resultado"));
  }

  @Test
  void telaPautasNovaNaoColideComPautaPorId() throws Exception {
    mvc.perform(get("/api/v1/telas/pautas/nova"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tipo").value("FORMULARIO"));
  }

  @Test
  void telaVotoOpcoesRetornaSelecaoComPayload() throws Exception {
    UUID pauta = UUID.randomUUID();
    mvc.perform(
            post("/api/v1/telas/pautas/{id}/voto/opcoes", pauta)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"associadoId\":\"a1\",\"cpf\":\"19839091069\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tipo").value("SELECAO"))
        .andExpect(jsonPath("$.itens[0].body.opcao").value("SIM"))
        .andExpect(jsonPath("$.itens[0].body.associadoId").value("a1"))
        .andExpect(
            jsonPath("$.itens[0].url")
                .value("http://localhost:8080/api/v1/pautas/" + pauta + "/votos"));
  }

  @Test
  void telaVotoOpcoesComCpfAusenteRetorna400() throws Exception {
    UUID pauta = UUID.randomUUID();
    mvc.perform(
            post("/api/v1/telas/pautas/{id}/voto/opcoes", pauta)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"associadoId\":\"a1\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.type").value("urn:vote:validacao"));
  }
}
