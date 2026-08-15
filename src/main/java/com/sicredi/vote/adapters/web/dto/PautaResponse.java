package com.sicredi.vote.adapters.web.dto;

import com.sicredi.vote.domain.model.Pauta;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representacao de uma pauta cadastrada")
public record PautaResponse(
    @Schema(
            description = "Identificador da pauta",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
    @Schema(description = "Titulo da pauta", example = "Reforma do estatuto social") String titulo,
    @Schema(
            description = "Descricao detalhada da pauta",
            example = "Proposta de atualizacao dos artigos 12 a 18 do estatuto")
        String descricao,
    @Schema(description = "Momento de criacao da pauta (UTC)", example = "2026-08-14T13:30:00Z")
        Instant criadaEm) {
  public static PautaResponse from(Pauta p) {
    return new PautaResponse(p.getId(), p.getTitulo(), p.getDescricao(), p.getCriadaEm());
  }
}
