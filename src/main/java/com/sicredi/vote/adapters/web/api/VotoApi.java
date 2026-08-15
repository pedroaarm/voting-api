package com.sicredi.vote.adapters.web.api;

import com.sicredi.vote.adapters.web.dto.RegistrarVotoRequest;
import com.sicredi.vote.adapters.web.dto.VotoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Votos")
public interface VotoApi {

  @Operation(summary = "Registra um voto do associado na pauta")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Voto registrado"),
    @ApiResponse(
        responseCode = "400",
        description = "Requisicao invalida",
        content =
            @Content(
                mediaType = OpenApiExamples.PROBLEM_JSON,
                examples = @ExampleObject(value = OpenApiExamples.VALIDACAO_CPF))),
    @ApiResponse(
        responseCode = "409",
        description = "Associado ja votou",
        content =
            @Content(
                mediaType = OpenApiExamples.PROBLEM_JSON,
                examples = @ExampleObject(value = OpenApiExamples.VOTO_DUPLICADO))),
    @ApiResponse(
        responseCode = "422",
        description = "Sessao fechada ou associado inelegivel",
        content =
            @Content(
                mediaType = OpenApiExamples.PROBLEM_JSON,
                examples = @ExampleObject(value = OpenApiExamples.SESSAO_FECHADA))),
    @ApiResponse(
        responseCode = "503",
        description = "Servico de elegibilidade indisponivel",
        content =
            @Content(
                mediaType = OpenApiExamples.PROBLEM_JSON,
                examples = @ExampleObject(value = OpenApiExamples.ELEGIBILIDADE_INDISPONIVEL)))
  })
  VotoResponse votar(@PathVariable UUID pautaId, @Valid @RequestBody RegistrarVotoRequest req);
}
