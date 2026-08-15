package com.sicredi.vote.adapters.persistence;

import com.sicredi.vote.application.exception.VotoDuplicadoException;
import com.sicredi.vote.application.port.out.PautaRepository;
import com.sicredi.vote.application.port.out.VotoRepository;
import com.sicredi.vote.domain.model.OpcaoVoto;
import com.sicredi.vote.domain.model.Pauta;
import com.sicredi.vote.domain.model.Voto;
import com.sicredi.vote.support.AbstractPostgresIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VotoConcorrenciaIT extends AbstractPostgresIT {

    @Autowired PautaRepository pautas;
    @Autowired VotoRepository votos;

    @Test
    void cinquentaThreadsMesmoAssociadoResultamEmUmVoto() throws Exception {
        Instant agora = Instant.now();
        UUID pauta = UUID.randomUUID();
        pautas.salvar(Pauta.builder().id(pauta).titulo("p").criadaEm(agora).build());

        int threads = 50;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch largada = new CountDownLatch(1);
        AtomicInteger sucessos = new AtomicInteger();
        AtomicInteger duplicados = new AtomicInteger();
        CountDownLatch fim = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try {
                    largada.await();
                    votos.salvar(Voto.builder().id(UUID.randomUUID()).pautaId(pauta)
                        .associadoId("assoc-unico").opcao(OpcaoVoto.SIM).criadoEm(agora).build());
                    sucessos.incrementAndGet();
                } catch (VotoDuplicadoException e) {
                    duplicados.incrementAndGet();
                } catch (Exception ignored) {
                    duplicados.incrementAndGet();
                } finally {
                    fim.countDown();
                }
            });
        }

        largada.countDown();
        assertThat(fim.await(30, TimeUnit.SECONDS)).isTrue();
        pool.shutdown();

        assertThat(sucessos.get()).isEqualTo(1);
        assertThat(votos.contar(pauta, OpcaoVoto.SIM)).isEqualTo(1);
        assertThat(sucessos.get() + duplicados.get()).isEqualTo(threads);
    }
}
