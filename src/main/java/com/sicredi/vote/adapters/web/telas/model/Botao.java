package com.sicredi.vote.adapters.web.telas.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record Botao(String texto, String url, Map<String, Object> body) {}
