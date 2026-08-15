package com.sicredi.vote.adapters.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.sicredi.vote.application.exception.SessaoEmAndamentoException;
import com.sicredi.vote.application.usecase.ConsultarResultado;
import com.sicredi.vote.domain.model.ResultadoVotacao;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ResultadoController.class)
@Import(ProblemDetailAdvice.class)
class ResultadoControllerTest {

  @Autowired MockMvc mvc;
  @MockitoBean ConsultarResultado consultarResultado;
  @MockitoBean VoteMetrics metrics;

  @Test
  void resultadoRetorna200() throws Exception {
    UUID pauta = UUID.randomUUID();
    when(consultarResultado.executar(pauta)).thenReturn(ResultadoVotacao.apurar(pauta, 3, 1));
    mvc.perform(get("/api/v1/pautas/" + pauta + "/resultado"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("APROVADA"))
        .andExpect(jsonPath("$.totalSim").value(3))
        .andExpect(jsonPath("$.total").value(4));
  }

  @Test
  void resultadoComSessaoEmAndamentoRetorna409() throws Exception {
    UUID pauta = UUID.randomUUID();
    when(consultarResultado.executar(any())).thenThrow(new SessaoEmAndamentoException(pauta));
    mvc.perform(get("/api/v1/pautas/" + pauta + "/resultado"))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.type").value("urn:vote:sessao-em-andamento"));
  }
}
