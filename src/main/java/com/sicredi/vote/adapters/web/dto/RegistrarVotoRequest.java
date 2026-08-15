package com.sicredi.vote.adapters.web.dto;

import com.sicredi.vote.domain.model.OpcaoVoto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Dados do voto de um associado em uma pauta")
public record RegistrarVotoRequest(
    @Schema(
            description = "Identificador do associado",
            example = "associado-42",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String associadoId,
    @Schema(
            description = "CPF do associado (apenas digitos)",
            example = "12345678900",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank
        String cpf,
    @Schema(
            description = "Opcao do voto",
            example = "SIM",
            requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        OpcaoVoto opcao) {}
