package com.sicredi.vote.adapters.web;

import com.sicredi.vote.adapters.web.api.VotoApi;
import com.sicredi.vote.adapters.web.dto.RegistrarVotoRequest;
import com.sicredi.vote.adapters.web.dto.VotoResponse;
import com.sicredi.vote.application.usecase.RegistrarVoto;
import com.sicredi.vote.domain.model.Voto;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/votos")
public class VotoController implements VotoApi {

  private final RegistrarVoto registrarVoto;
  private final VoteMetrics metrics;

  public VotoController(RegistrarVoto registrarVoto, VoteMetrics metrics) {
    this.registrarVoto = registrarVoto;
    this.metrics = metrics;
  }

  @Override
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public VotoResponse votar(
      @PathVariable UUID pautaId, @Valid @RequestBody RegistrarVotoRequest req) {
    Voto voto = registrarVoto.executar(pautaId, req.associadoId(), req.cpf(), req.opcao());
    metrics.votoRegistrado();
    return VotoResponse.from(voto);
  }
}
