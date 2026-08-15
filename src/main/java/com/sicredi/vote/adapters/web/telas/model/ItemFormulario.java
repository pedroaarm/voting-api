package com.sicredi.vote.adapters.web.telas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ItemFormulario(String tipo, String texto, String id, String titulo, String valor) {}
