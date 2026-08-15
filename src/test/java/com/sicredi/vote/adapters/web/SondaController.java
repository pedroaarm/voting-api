package com.sicredi.vote.adapters.web;

import com.sicredi.vote.application.exception.*;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller-sonda usado apenas em testes para exercitar o {@link ProblemDetailAdvice} (ver
 * ProblemDetailAdviceTest). Precisa ser uma classe top-level: em Spring Boot
 * 4.1.0, @WebMvcTest(controllers = ...) nao registra controllers que sao classes estaticas
 * aninhadas dentro da propria classe de teste (o component-scan da fatia ignora-as
 * silenciosamente), entao a sonda foi extraida para este arquivo.
 */
@RestController
class SondaController {
  @GetMapping("/sonda/pauta")
  String pauta() {
    throw new PautaNaoEncontradaException(UUID.randomUUID());
  }

  @GetMapping("/sonda/dup")
  String dup() {
    throw new VotoDuplicadoException("assoc-1");
  }

  @GetMapping("/sonda/fechada")
  String fechada() {
    throw new SessaoFechadaException(UUID.randomUUID());
  }

  @GetMapping("/sonda/indisp")
  String indisp() {
    throw new ElegibilidadeIndisponivelException("fora");
  }
}
