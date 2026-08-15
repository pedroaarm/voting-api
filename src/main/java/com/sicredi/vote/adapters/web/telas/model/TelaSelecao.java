package com.sicredi.vote.adapters.web.telas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record TelaSelecao(String tipo, String titulo, List<ItemSelecao> itens) {}
