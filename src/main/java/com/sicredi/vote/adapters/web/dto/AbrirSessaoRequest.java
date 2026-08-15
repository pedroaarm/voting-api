package com.sicredi.vote.adapters.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Positive;

@Schema(description = "Parametros para abertura de uma sessao de votacao")
public record AbrirSessaoRequest(
    @Schema(description = "Duracao da sessao em minutos. Se omitido, usa o padrao configurado (1 minuto)",
            example = "5", nullable = true)
    @Positive Integer duracaoMinutos
) {}
