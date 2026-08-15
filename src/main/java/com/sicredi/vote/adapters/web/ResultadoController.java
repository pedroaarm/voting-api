package com.sicredi.vote.adapters.web;

import com.sicredi.vote.adapters.web.dto.ResultadoResponse;
import com.sicredi.vote.application.usecase.ConsultarResultado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Tag(name = "Resultado")
@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/resultado")
public class ResultadoController {

    private final ConsultarResultado consultarResultado;

    public ResultadoController(ConsultarResultado consultarResultado) {
        this.consultarResultado = consultarResultado;
    }

    @Operation(summary = "Consulta o resultado da votacao de uma pauta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Resultado apurado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"pautaId\":\"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\"status\":\"APROVADA\",\"totalSim\":10,\"totalNao\":3,\"total\":13}"))),
        @ApiResponse(responseCode = "404", description = "Pauta nao encontrada",
            content = @Content(mediaType = "application/problem+json",
                examples = @ExampleObject(value = "{\"type\":\"urn:vote:pauta-nao-encontrada\",\"title\":\"Pauta nao encontrada\",\"status\":404}"))),
        @ApiResponse(responseCode = "409", description = "Sessao ainda em andamento",
            content = @Content(mediaType = "application/problem+json",
                examples = @ExampleObject(value = "{\"type\":\"urn:vote:sessao-em-andamento\",\"title\":\"Sessao em andamento\",\"status\":409}")))
    })
    @GetMapping
    public ResultadoResponse resultado(@PathVariable UUID pautaId) {
        return ResultadoResponse.from(consultarResultado.executar(pautaId));
    }
}
