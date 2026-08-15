package com.sicredi.vote.adapters.web;

import com.sicredi.vote.application.exception.*;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
    throw new ElegibilidadeIndisponivelException();
  }
}
