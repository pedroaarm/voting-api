package com.sicredi.vote.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.util.UUID;

@Getter
@Builder
public class ResultadoVotacao {
    private final UUID pautaId;
    private final long totalSim;
    private final long totalNao;
    private final StatusResultado status;

    public long getTotal() {
        return totalSim + totalNao;
    }

    public static ResultadoVotacao apurar(UUID pautaId, long totalSim, long totalNao) {
        StatusResultado status;
        if (totalSim > totalNao) {
            status = StatusResultado.APROVADA;
        } else if (totalNao > totalSim) {
            status = StatusResultado.REJEITADA;
        } else {
            status = StatusResultado.EMPATE;
        }
        return ResultadoVotacao.builder()
            .pautaId(pautaId).totalSim(totalSim).totalNao(totalNao).status(status).build();
    }
}
