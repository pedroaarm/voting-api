package com.sicredi.vote.adapters.gateway;

import com.sicredi.vote.application.port.out.Elegibilidade;
import com.sicredi.vote.application.port.out.VerificadorElegibilidade;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "vote.elegibilidade.provider", havingValue = "sempre", matchIfMissing = true)
public class SempreElegivel implements VerificadorElegibilidade {
    @Override
    public Elegibilidade verificar(String cpf) {
        return Elegibilidade.ABLE_TO_VOTE;
    }
}
