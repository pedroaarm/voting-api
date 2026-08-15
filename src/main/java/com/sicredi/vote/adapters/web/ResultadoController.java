package com.sicredi.vote.adapters.web;

import com.sicredi.vote.adapters.web.api.ResultadoApi;
import com.sicredi.vote.adapters.web.dto.ResultadoResponse;
import com.sicredi.vote.application.usecase.ConsultarResultado;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/resultado")
public class ResultadoController implements ResultadoApi {

  private final ConsultarResultado consultarResultado;

  public ResultadoController(ConsultarResultado consultarResultado) {
    this.consultarResultado = consultarResultado;
  }

  @Override
  @GetMapping
  public ResultadoResponse resultado(@PathVariable UUID pautaId) {
    return ResultadoResponse.from(consultarResultado.executar(pautaId));
  }
}
