package com.sicredi.vote.adapters.web.telas;

import com.sicredi.vote.adapters.web.dto.VotoOpcoesRequest;
import com.sicredi.vote.adapters.web.telas.model.TelaFormulario;
import com.sicredi.vote.adapters.web.telas.model.TelaSelecao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(
    name = "Telas",
    description =
        "Endpoints server-driven que descrevem as telas do fluxo (menu, formularios e selecoes) para o cliente")
public interface TelaApi {

  @Operation(
      summary = "Tela de menu inicial",
      description = "Retorna a tela de selecao com as acoes disponiveis no fluxo")
  TelaSelecao menu();

  @Operation(
      summary = "Tela de cadastro de pauta",
      description = "Retorna o formulario para cadastrar uma nova pauta")
  TelaFormulario novaPauta();

  @Operation(
      summary = "Tela de listagem de pautas",
      description = "Retorna a tela de selecao com as pautas cadastradas")
  TelaSelecao pautas();

  @Operation(
      summary = "Tela de acoes da pauta",
      description = "Retorna a tela de selecao com as acoes disponiveis para a pauta informada")
  TelaSelecao acoesPauta(@PathVariable UUID pautaId);

  @Operation(
      summary = "Tela de abertura de sessao",
      description = "Retorna o formulario para abrir uma sessao de votacao da pauta")
  TelaFormulario abrirSessao(@PathVariable UUID pautaId);

  @Operation(
      summary = "Tela de identificacao para voto",
      description = "Retorna o formulario de identificacao do associado antes do voto")
  TelaFormulario voto(@PathVariable UUID pautaId);

  @Operation(
      summary = "Tela de opcoes de voto",
      description =
          "Recebe a identificacao do associado e retorna a tela de selecao com as opcoes de voto")
  TelaSelecao votoOpcoes(@PathVariable UUID pautaId, @Valid @RequestBody VotoOpcoesRequest req);

  @Operation(
      summary = "Tela de resultado",
      description = "Retorna a tela com o resultado apurado da votacao da pauta")
  TelaFormulario resultado(@PathVariable UUID pautaId);
}
