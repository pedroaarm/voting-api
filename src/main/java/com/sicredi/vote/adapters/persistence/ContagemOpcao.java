package com.sicredi.vote.adapters.persistence;

import com.sicredi.vote.domain.model.OpcaoVoto;
public interface ContagemOpcao {
    OpcaoVoto getOpcao();
    long getTotal();
}
