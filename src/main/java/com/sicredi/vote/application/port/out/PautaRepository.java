package com.sicredi.vote.application.port.out;

import com.sicredi.vote.domain.model.Pauta;
import java.util.Optional;
import java.util.UUID;

public interface PautaRepository {
    Pauta salvar(Pauta pauta);
    Optional<Pauta> buscarPorId(UUID id);
    java.util.List<com.sicredi.vote.domain.model.Pauta> listarTodas();
}
