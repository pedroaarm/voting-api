package com.sicredi.vote.adapters.web.dto;

import com.sicredi.vote.domain.model.ResultadoVotacao;
import com.sicredi.vote.domain.model.StatusResultado;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Resultado apurado da votacao de uma pauta")
public record ResultadoResponse(
    @Schema(
            description = "Identificador da pauta apurada",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID pautaId,
    @Schema(description = "Desfecho da votacao", example = "APROVADA") StatusResultado status,
    @Schema(description = "Total de votos SIM", example = "10") long totalSim,
    @Schema(description = "Total de votos NAO", example = "3") long totalNao,
    @Schema(description = "Total de votos computados", example = "13") long total) {
  public static ResultadoResponse from(ResultadoVotacao r) {
    return new ResultadoResponse(
        r.getPautaId(), r.getStatus(), r.getTotalSim(), r.getTotalNao(), r.getTotal());
  }
}
