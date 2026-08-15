package com.sicredi.vote.adapters.web.dto;

import com.sicredi.vote.domain.model.OpcaoVoto;
import com.sicredi.vote.domain.model.Voto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Confirmacao de um voto registrado")
public record VotoResponse(
    @Schema(description = "Identificador do voto", example = "b7f8e2a1-3c4d-4e5f-8a9b-0c1d2e3f4a5b")
        UUID id,
    @Schema(
            description = "Identificador da pauta votada",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID pautaId,
    @Schema(description = "Opcao registrada", example = "SIM") OpcaoVoto opcao) {
  public static VotoResponse from(Voto v) {
    return new VotoResponse(v.getId(), v.getPautaId(), v.getOpcao());
  }
}
