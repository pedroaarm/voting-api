package com.sicredi.vote.adapters.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import com.sicredi.vote.application.port.out.VerificadorElegibilidade;
import com.sicredi.vote.support.AbstractPostgresIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ElegibilidadeProviderIT extends AbstractPostgresIT {

  @Autowired VerificadorElegibilidade verificador;

  @Test
  void providerDefaultUsaSempreElegivel() {
    assertThat(verificador).isInstanceOf(SempreElegivel.class);
  }
}
