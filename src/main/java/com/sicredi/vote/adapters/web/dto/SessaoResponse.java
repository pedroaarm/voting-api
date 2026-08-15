package com.sicredi.vote.adapters.web.dto;

import com.sicredi.vote.domain.model.Sessao;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

@Schema(description = "Representacao de uma sessao de votacao aberta")
public record SessaoResponse(
    @Schema(description = "Identificador da sessao", example = "7c9e6679-7425-40de-944b-e07fc1f90ae7")
    UUID id,

    @Schema(description = "Identificador da pauta associada", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    UUID pautaId,

    @Schema(description = "Momento de abertura da sessao (UTC)", example = "2026-08-14T13:30:00Z")
    Instant abertura,

    @Schema(description = "Momento de fechamento da sessao (UTC)", example = "2026-08-14T13:35:00Z")
    Instant fechamento
) {
    public static SessaoResponse from(Sessao s) {
        return new SessaoResponse(s.getId(), s.getPautaId(), s.getAbertura(), s.getFechamento());
    }
}
