package com.sicredi.vote.adapters.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SondaController.class)
@Import({ProblemDetailAdvice.class})
class ProblemDetailAdviceTest {

  @Autowired MockMvc mvc;
  @MockitoBean VoteMetrics metrics;

  @Test
  void pautaNaoEncontradaVira404ComTypeEstavel() throws Exception {
    mvc.perform(get("/sonda/pauta"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.type").value("urn:vote:pauta-nao-encontrada"))
        .andExpect(jsonPath("$.title").value("Pauta nao encontrada"));
  }

  @Test
  void votoDuplicadoVira409() throws Exception {
    mvc.perform(get("/sonda/dup"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.type").value("urn:vote:voto-duplicado"));
  }

  @Test
  void sessaoFechadaVira422() throws Exception {
    mvc.perform(get("/sonda/fechada"))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.type").value("urn:vote:sessao-fechada"));
  }

  @Test
  void elegibilidadeIndisponivelVira503() throws Exception {
    mvc.perform(get("/sonda/indisp"))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.type").value("urn:vote:elegibilidade-indisponivel"));
  }
}
