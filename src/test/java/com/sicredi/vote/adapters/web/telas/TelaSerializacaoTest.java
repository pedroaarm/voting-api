package com.sicredi.vote.adapters.web.telas;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sicredi.vote.adapters.web.telas.model.*;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TelaSerializacaoTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  void formularioSerializaSemCamposNulos() throws Exception {
    var tela =
        TelaFormulario.builder()
            .tipo("FORMULARIO")
            .titulo("T")
            .itens(
                List.of(
                    ItemFormulario.builder()
                        .tipo("INPUT_TEXTO")
                        .id("cpf")
                        .titulo("CPF")
                        .valor("")
                        .build()))
            .botaoOk(
                Botao.builder()
                    .texto("Sim")
                    .url("http://x/votos")
                    .body(Map.of("opcao", "SIM"))
                    .build())
            .build();
    String json = mapper.writeValueAsString(tela);
    assertThat(json).contains("\"tipo\":\"FORMULARIO\"").contains("\"botaoOk\"");
    assertThat(json).doesNotContain("botaoCancelar");
    assertThat(json).doesNotContain("\"texto\":null");
  }

  @Test
  void selecaoSerializaItensComUrlEBody() throws Exception {
    var tela =
        TelaSelecao.builder()
            .tipo("SELECAO")
            .titulo("Voto")
            .itens(
                List.of(
                    ItemSelecao.builder()
                        .texto("Sim")
                        .url("http://x/votos")
                        .body(Map.of("opcao", "SIM"))
                        .build(),
                    ItemSelecao.builder()
                        .texto("Nao")
                        .url("http://x/votos")
                        .body(Map.of("opcao", "NAO"))
                        .build()))
            .build();
    String json = mapper.writeValueAsString(tela);
    assertThat(json).contains("\"tipo\":\"SELECAO\"").contains("\"opcao\":\"SIM\"");
  }
}
