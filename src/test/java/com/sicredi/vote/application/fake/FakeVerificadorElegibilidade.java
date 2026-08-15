package com.sicredi.vote.application.fake;

import com.sicredi.vote.application.exception.ElegibilidadeIndisponivelException;
import com.sicredi.vote.application.port.out.Elegibilidade;
import com.sicredi.vote.application.port.out.VerificadorElegibilidade;

public class FakeVerificadorElegibilidade implements VerificadorElegibilidade {
  public Elegibilidade resposta = Elegibilidade.ABLE_TO_VOTE;
  public boolean indisponivel = false;

  @Override
  public Elegibilidade verificar(String cpf) {
    if (indisponivel) throw new ElegibilidadeIndisponivelException();
    return resposta;
  }
}
