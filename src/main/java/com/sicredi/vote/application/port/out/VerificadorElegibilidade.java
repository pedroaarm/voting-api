package com.sicredi.vote.application.port.out;

public interface VerificadorElegibilidade {
  Elegibilidade verificar(String cpf);
}
