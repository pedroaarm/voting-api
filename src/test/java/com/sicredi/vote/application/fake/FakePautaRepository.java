package com.sicredi.vote.application.fake;

import com.sicredi.vote.application.port.out.PautaRepository;
import com.sicredi.vote.domain.model.Pauta;
import java.util.*;

public class FakePautaRepository implements PautaRepository {
    public final Map<UUID, Pauta> dados = new HashMap<>();
    @Override public Pauta salvar(Pauta p) { dados.put(p.getId(), p); return p; }
    @Override public Optional<Pauta> buscarPorId(UUID id) { return Optional.ofNullable(dados.get(id)); }
    @Override public java.util.List<com.sicredi.vote.domain.model.Pauta> listarTodas() {
        return new java.util.ArrayList<>(dados.values());
    }
}
