package com.sicredi.vote.adapters.web;

import com.sicredi.vote.adapters.web.dto.*;
import com.sicredi.vote.application.usecase.AbrirSessao;
import com.sicredi.vote.application.usecase.CadastrarPauta;
import com.sicredi.vote.domain.model.Pauta;
import com.sicredi.vote.domain.model.Sessao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.UUID;

@Tag(name = "Pautas")
@RestController
@RequestMapping("/api/v1/pautas")
public class PautaController {

    private final CadastrarPauta cadastrarPauta;
    private final AbrirSessao abrirSessao;

    public PautaController(CadastrarPauta cadastrarPauta, AbrirSessao abrirSessao) {
        this.cadastrarPauta = cadastrarPauta;
        this.abrirSessao = abrirSessao;
    }

    @Operation(summary = "Cadastra uma nova pauta")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pauta cadastrada"),
        @ApiResponse(responseCode = "400", description = "Requisicao invalida",
            content = @Content(mediaType = "application/problem+json",
                examples = @ExampleObject(value = "{\"type\":\"urn:vote:validacao\",\"title\":\"Requisicao invalida\",\"status\":400,\"errors\":[\"titulo: nao pode ser vazio\"]}")))
    })
    @PostMapping
    public ResponseEntity<PautaResponse> cadastrar(@Valid @RequestBody CadastrarPautaRequest req) {
        Pauta pauta = cadastrarPauta.executar(req.titulo(), req.descricao());
        return ResponseEntity.created(URI.create("/api/v1/pautas/" + pauta.getId()))
            .body(PautaResponse.from(pauta));
    }

    @Operation(summary = "Abre uma sessao de votacao para a pauta")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Sessao aberta"),
        @ApiResponse(responseCode = "400", description = "Requisicao invalida",
            content = @Content(mediaType = "application/problem+json",
                examples = @ExampleObject(value = "{\"type\":\"urn:vote:validacao\",\"title\":\"Requisicao invalida\",\"status\":400,\"errors\":[\"duracaoMinutos: deve ser positivo\"]}"))),
        @ApiResponse(responseCode = "404", description = "Pauta nao encontrada",
            content = @Content(mediaType = "application/problem+json",
                examples = @ExampleObject(value = "{\"type\":\"urn:vote:pauta-nao-encontrada\",\"title\":\"Pauta nao encontrada\",\"status\":404}"))),
        @ApiResponse(responseCode = "409", description = "Sessao ja aberta para esta pauta",
            content = @Content(mediaType = "application/problem+json",
                examples = @ExampleObject(value = "{\"type\":\"urn:vote:sessao-ja-aberta\",\"title\":\"Sessao ja aberta\",\"status\":409}")))
    })
    @PostMapping("/{pautaId}/sessoes")
    public ResponseEntity<SessaoResponse> abrirSessao(@PathVariable UUID pautaId,
                                                      @Valid @RequestBody AbrirSessaoRequest req) {
        Sessao sessao = abrirSessao.executar(pautaId, req.duracaoMinutos());
        return ResponseEntity.created(URI.create("/api/v1/pautas/" + pautaId + "/sessoes/" + sessao.getId()))
            .body(SessaoResponse.from(sessao));
    }
}
