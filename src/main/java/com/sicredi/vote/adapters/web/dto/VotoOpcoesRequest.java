package com.sicredi.vote.adapters.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Identificacao do associado para exibir a tela de opcoes de voto")
public record VotoOpcoesRequest(
    @Schema(description = "Identificador do associado", example = "associado-42", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank String associadoId,

    @Schema(description = "CPF do associado (apenas digitos)", example = "12345678900", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank String cpf
) {}
