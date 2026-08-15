package com.sicredi.vote.application.fake;

import com.sicredi.vote.application.port.out.SessaoRepository;
import com.sicredi.vote.domain.model.Sessao;
import java.util.*;

public class FakeSessaoRepository implements SessaoRepository {
  public final Map<UUID, Sessao> porPauta = new HashMap<>();

  @Override
  public Sessao salvar(Sessao s) {
    porPauta.put(s.getPautaId(), s);
    return s;
  }

  @Override
  public Optional<Sessao> buscarPorPauta(UUID pautaId) {
    return Optional.ofNullable(porPauta.get(pautaId));
  }

  @Override
  public boolean existePorPauta(UUID pautaId) {
    return porPauta.containsKey(pautaId);
  }
}
