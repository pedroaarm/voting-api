package com.sicredi.vote.application.fake;

import com.sicredi.vote.application.exception.VotoDuplicadoException;
import com.sicredi.vote.application.port.out.VotoRepository;
import com.sicredi.vote.domain.model.OpcaoVoto;
import com.sicredi.vote.domain.model.Voto;
import java.util.*;

public class FakeVotoRepository implements VotoRepository {
    public final List<Voto> dados = new ArrayList<>();
    private final Set<String> chaves = new HashSet<>();

    @Override public Voto salvar(Voto v) {
        String chave = v.getPautaId() + "|" + v.getAssociadoId();
        if (!chaves.add(chave)) throw new VotoDuplicadoException(v.getAssociadoId());
        dados.add(v);
        return v;
    }
    @Override public long contar(UUID pautaId, OpcaoVoto opcao) {
        return dados.stream().filter(v -> v.getPautaId().equals(pautaId) && v.getOpcao() == opcao).count();
    }
}
