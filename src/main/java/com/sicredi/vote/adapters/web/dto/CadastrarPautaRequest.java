package com.sicredi.vote.adapters.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para cadastro de uma nova pauta de votacao")
public record CadastrarPautaRequest(
    @Schema(description = "Titulo da pauta", example = "Reforma do estatuto social", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank String titulo,

    @Schema(description = "Descricao detalhada da pauta", example = "Proposta de atualizacao dos artigos 12 a 18 do estatuto")
    String descricao
) {}
