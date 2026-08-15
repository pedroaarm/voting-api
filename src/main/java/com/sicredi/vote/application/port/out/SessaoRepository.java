package com.sicredi.vote.application.port.out;

import com.sicredi.vote.domain.model.Sessao;
import java.util.Optional;
import java.util.UUID;

public interface SessaoRepository {
  Sessao salvar(Sessao sessao);

  Optional<Sessao> buscarPorPauta(UUID pautaId);

  boolean existePorPauta(UUID pautaId);
}
