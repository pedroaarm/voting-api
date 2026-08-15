package com.sicredi.vote.adapters.web.telas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record TelaFormulario(
    String tipo, String titulo, List<ItemFormulario> itens, Botao botaoOk, Botao botaoCancelar) {}
