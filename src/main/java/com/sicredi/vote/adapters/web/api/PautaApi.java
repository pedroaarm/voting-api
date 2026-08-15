package com.sicredi.vote.adapters.web.api;

import com.sicredi.vote.adapters.web.dto.AbrirSessaoRequest;
import com.sicredi.vote.adapters.web.dto.CadastrarPautaRequest;
import com.sicredi.vote.adapters.web.dto.PautaResponse;
import com.sicredi.vote.adapters.web.dto.SessaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Pautas")
public interface PautaApi {

  @Operation(summary = "Cadastra uma nova pauta")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Pauta cadastrada"),
    @ApiResponse(
        responseCode = "400",
        description = "Requisicao invalida",
        content =
            @Content(
                mediaType = OpenApiExamples.PROBLEM_JSON,
                examples = @ExampleObject(value = OpenApiExamples.VALIDACAO_TITULO)))
  })
  ResponseEntity<PautaResponse> cadastrar(@Valid @RequestBody CadastrarPautaRequest req);

  @Operation(summary = "Abre uma sessao de votacao para a pauta")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Sessao aberta"),
    @ApiResponse(
        responseCode = "400",
        description = "Requisicao invalida",
        content =
            @Content(
                mediaType = OpenApiExamples.PROBLEM_JSON,
                examples = @ExampleObject(value = OpenApiExamples.VALIDACAO_DURACAO))),
    @ApiResponse(
        responseCode = "404",
        description = "Pauta nao encontrada",
        content =
            @Content(
                mediaType = OpenApiExamples.PROBLEM_JSON,
                examples = @ExampleObject(value = OpenApiExamples.PAUTA_NAO_ENCONTRADA))),
    @ApiResponse(
        responseCode = "409",
        description = "Sessao ja aberta para esta pauta",
        content =
            @Content(
                mediaType = OpenApiExamples.PROBLEM_JSON,
                examples = @ExampleObject(value = OpenApiExamples.SESSAO_JA_ABERTA)))
  })
  ResponseEntity<SessaoResponse> abrirSessao(
      @PathVariable UUID pautaId, @Valid @RequestBody AbrirSessaoRequest req);
}
