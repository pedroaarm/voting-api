package com.sicredi.vote.adapters.web;

import com.sicredi.vote.adapters.web.dto.RegistrarVotoRequest;
import com.sicredi.vote.adapters.web.dto.VotoResponse;
import com.sicredi.vote.application.usecase.RegistrarVoto;
import com.sicredi.vote.domain.model.Voto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Tag(name = "Votos")
@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/votos")
public class VotoController {

    private final RegistrarVoto registrarVoto;
    private final VoteMetrics metrics;

    public VotoController(RegistrarVoto registrarVoto, VoteMetrics metrics) {
        this.registrarVoto = registrarVoto;
        this.metrics = metrics;
    }

    @Operation(summary = "Registra um voto do associado na pauta")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Voto registrado"),
        @ApiResponse(responseCode = "400", description = "Requisicao invalida",
            content = @Content(mediaType = "application/problem+json",
                examples = @ExampleObject(value = "{\"type\":\"urn:vote:validacao\",\"title\":\"Requisicao invalida\",\"status\":400,\"errors\":[\"cpf: nao pode ser vazio\"]}"))),
        @ApiResponse(responseCode = "409", description = "Associado ja votou",
            content = @Content(mediaType = "application/problem+json",
                examples = @ExampleObject(value = "{\"type\":\"urn:vote:voto-duplicado\",\"title\":\"Voto duplicado\",\"status\":409}"))),
        @ApiResponse(responseCode = "422", description = "Sessao fechada ou associado inelegivel",
            content = @Content(mediaType = "application/problem+json",
                examples = @ExampleObject(value = "{\"type\":\"urn:vote:sessao-fechada\",\"title\":\"Sessao fechada\",\"status\":422}"))),
        @ApiResponse(responseCode = "503", description = "Servico de elegibilidade indisponivel",
            content = @Content(mediaType = "application/problem+json",
                examples = @ExampleObject(value = "{\"type\":\"urn:vote:elegibilidade-indisponivel\",\"title\":\"Servico de elegibilidade indisponivel\",\"status\":503}")))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VotoResponse votar(@PathVariable UUID pautaId, @Valid @RequestBody RegistrarVotoRequest req) {
        Voto voto = registrarVoto.executar(pautaId, req.associadoId(), req.cpf(), req.opcao());
        metrics.votoRegistrado();
        return VotoResponse.from(voto);
    }
}
