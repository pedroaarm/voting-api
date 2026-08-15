package com.sicredi.vote.adapters.web.telas;

import com.sicredi.vote.adapters.web.dto.VotoOpcoesRequest;
import com.sicredi.vote.adapters.web.telas.model.TelaFormulario;
import com.sicredi.vote.adapters.web.telas.model.TelaSelecao;
import com.sicredi.vote.application.usecase.ConsultarResultado;
import com.sicredi.vote.application.usecase.ListarPautas;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/telas")
public class TelaController implements TelaApi {

  private final TelaFactory telas;
  private final ConsultarResultado consultarResultado;
  private final ListarPautas listarPautas;

  public TelaController(
      TelaFactory telas, ConsultarResultado consultarResultado, ListarPautas listarPautas) {
    this.telas = telas;
    this.consultarResultado = consultarResultado;
    this.listarPautas = listarPautas;
  }

  @Override
  @GetMapping("/menu")
  public TelaSelecao menu() {
    return telas.menu();
  }

  @Override
  @GetMapping("/pautas/nova")
  public TelaFormulario novaPauta() {
    return telas.novaPauta();
  }

  @Override
  @GetMapping("/pautas")
  public TelaSelecao pautas() {
    return telas.listaPautas(listarPautas.executar());
  }

  @Override
  @GetMapping("/pautas/{pautaId}")
  public TelaSelecao acoesPauta(@PathVariable UUID pautaId) {
    return telas.acoesPauta(pautaId);
  }

  @Override
  @GetMapping("/pautas/{pautaId}/sessao/nova")
  public TelaFormulario abrirSessao(@PathVariable UUID pautaId) {
    return telas.abrirSessao(pautaId);
  }

  @Override
  @GetMapping("/pautas/{pautaId}/voto")
  public TelaFormulario voto(@PathVariable UUID pautaId) {
    return telas.votoIdentificacao(pautaId);
  }

  @Override
  @PostMapping("/pautas/{pautaId}/voto/opcoes")
  public TelaSelecao votoOpcoes(
      @PathVariable UUID pautaId, @Valid @RequestBody VotoOpcoesRequest req) {
    return telas.votoOpcoes(pautaId, req.associadoId(), req.cpf());
  }

  @Override
  @GetMapping("/pautas/{pautaId}/resultado")
  public TelaFormulario resultado(@PathVariable UUID pautaId) {
    return telas.resultado(consultarResultado.executar(pautaId));
  }
}
