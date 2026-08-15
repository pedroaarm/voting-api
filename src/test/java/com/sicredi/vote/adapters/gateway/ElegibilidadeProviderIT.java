package com.sicredi.vote.adapters.gateway;

import com.sicredi.vote.application.port.out.VerificadorElegibilidade;
import com.sicredi.vote.support.AbstractPostgresIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ElegibilidadeProviderIT extends AbstractPostgresIT {

    @Autowired VerificadorElegibilidade verificador;

    @Test
    void providerDefaultUsaSempreElegivel() {
        assertThat(verificador).isInstanceOf(SempreElegivel.class);
    }
}
