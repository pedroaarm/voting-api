package com.sicredi.vote.adapters.web;

import com.sicredi.vote.adapters.web.api.PautaApi;
import com.sicredi.vote.adapters.web.dto.*;
import com.sicredi.vote.application.usecase.AbrirSessao;
import com.sicredi.vote.application.usecase.CadastrarPauta;
import com.sicredi.vote.domain.model.Pauta;
import com.sicredi.vote.domain.model.Sessao;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas")
public class PautaController implements PautaApi {

  private final CadastrarPauta cadastrarPauta;
  private final AbrirSessao abrirSessao;

  public PautaController(CadastrarPauta cadastrarPauta, AbrirSessao abrirSessao) {
    this.cadastrarPauta = cadastrarPauta;
    this.abrirSessao = abrirSessao;
  }

  @Override
  @PostMapping
  public ResponseEntity<PautaResponse> cadastrar(@Valid @RequestBody CadastrarPautaRequest req) {
    Pauta pauta = cadastrarPauta.executar(req.titulo(), req.descricao());
    return ResponseEntity.created(URI.create("/api/v1/pautas/" + pauta.getId()))
        .body(PautaResponse.from(pauta));
  }

  @Override
  @PostMapping("/{pautaId}/sessoes")
  public ResponseEntity<SessaoResponse> abrirSessao(
      @PathVariable UUID pautaId, @Valid @RequestBody AbrirSessaoRequest req) {
    Sessao sessao = abrirSessao.executar(pautaId, req.duracaoMinutos());
    return ResponseEntity.created(
            URI.create("/api/v1/pautas/" + pautaId + "/sessoes/" + sessao.getId()))
        .body(SessaoResponse.from(sessao));
  }
}
