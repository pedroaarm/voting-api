package com.sicredi.vote.adapters.web.api;

import com.sicredi.vote.adapters.web.dto.ResultadoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Resultado")
public interface ResultadoApi {

  @Operation(summary = "Consulta o resultado da votacao de uma pauta")
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "Resultado apurado",
        content =
            @Content(
                mediaType = OpenApiExamples.JSON,
                examples = @ExampleObject(value = OpenApiExamples.RESULTADO))),
    @ApiResponse(
        responseCode = "404",
        description = "Pauta nao encontrada",
        content =
            @Content(
                mediaType = OpenApiExamples.PROBLEM_JSON,
                examples = @ExampleObject(value = OpenApiExamples.PAUTA_NAO_ENCONTRADA))),
    @ApiResponse(
        responseCode = "409",
        description = "Sessao ainda em andamento",
        content =
            @Content(
                mediaType = OpenApiExamples.PROBLEM_JSON,
                examples = @ExampleObject(value = OpenApiExamples.SESSAO_EM_ANDAMENTO)))
  })
  ResultadoResponse resultado(@PathVariable UUID pautaId);
}
