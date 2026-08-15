package com.sicredi.vote.application.port.out;

import com.sicredi.vote.domain.model.OpcaoVoto;
import com.sicredi.vote.domain.model.Voto;
import java.util.UUID;

public interface VotoRepository {
  Voto salvar(Voto voto);

  long contar(UUID pautaId, OpcaoVoto opcao);
}
