package com.sicredi.vote.adapters.web.telas;

import com.sicredi.vote.adapters.web.telas.model.TelaFormulario;
import com.sicredi.vote.adapters.web.telas.model.TelaSelecao;
import com.sicredi.vote.application.usecase.ConsultarResultado;
import com.sicredi.vote.application.usecase.ListarPautas;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@Tag(name = "Telas", description = "Endpoints server-driven que descrevem as telas do fluxo (menu, formularios e selecoes) para o cliente")
@RestController
@RequestMapping("/api/v1/telas")
public class TelaController {

    private final TelaFactory telas;
    private final ConsultarResultado consultarResultado;
    private final ListarPautas listarPautas;

    public TelaController(TelaFactory telas, ConsultarResultado consultarResultado, ListarPautas listarPautas) {
        this.telas = telas;
        this.consultarResultado = consultarResultado;
        this.listarPautas = listarPautas;
    }

    @Operation(summary = "Tela de menu inicial", description = "Retorna a tela de selecao com as acoes disponiveis no fluxo")
    @GetMapping("/menu")
    public TelaSelecao menu() { return telas.menu(); }

    @Operation(summary = "Tela de cadastro de pauta", description = "Retorna o formulario para cadastrar uma nova pauta")
    @GetMapping("/pautas/nova")
    public TelaFormulario novaPauta() { return telas.novaPauta(); }

    @Operation(summary = "Tela de listagem de pautas", description = "Retorna a tela de selecao com as pautas cadastradas")
    @GetMapping("/pautas")
    public TelaSelecao pautas() { return telas.listaPautas(listarPautas.executar()); }

    @Operation(summary = "Tela de acoes da pauta", description = "Retorna a tela de selecao com as acoes disponiveis para a pauta informada")
    @GetMapping("/pautas/{pautaId}")
    public TelaSelecao acoesPauta(@PathVariable UUID pautaId) { return telas.acoesPauta(pautaId); }

    @Operation(summary = "Tela de abertura de sessao", description = "Retorna o formulario para abrir uma sessao de votacao da pauta")
    @GetMapping("/pautas/{pautaId}/sessao/nova")
    public TelaFormulario abrirSessao(@PathVariable UUID pautaId) { return telas.abrirSessao(pautaId); }

    @Operation(summary = "Tela de identificacao para voto", description = "Retorna o formulario de identificacao do associado antes do voto")
    @GetMapping("/pautas/{pautaId}/voto")
    public TelaFormulario voto(@PathVariable UUID pautaId) { return telas.votoIdentificacao(pautaId); }

    @Operation(summary = "Tela de opcoes de voto", description = "Recebe a identificacao do associado e retorna a tela de selecao com as opcoes de voto")
    @PostMapping("/pautas/{pautaId}/voto/opcoes")
    public TelaSelecao votoOpcoes(@PathVariable UUID pautaId,
                                  @Valid @RequestBody com.sicredi.vote.adapters.web.dto.VotoOpcoesRequest req) {
        return telas.votoOpcoes(pautaId, req.associadoId(), req.cpf());
    }

    @Operation(summary = "Tela de resultado", description = "Retorna a tela com o resultado apurado da votacao da pauta")
    @GetMapping("/pautas/{pautaId}/resultado")
    public TelaFormulario resultado(@PathVariable UUID pautaId) {
        return telas.resultado(consultarResultado.executar(pautaId));
    }
}
