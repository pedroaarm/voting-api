package com.sicredi.vote.adapters.web;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class VoteMetrics {

    private final Counter registrados;
    private final MeterRegistry registry;

    public VoteMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.registrados = Counter.builder("vote.registrados").description("Votos registrados").register(registry);
    }

    public void votoRegistrado() { registrados.increment(); }

    public void votoRecusado(String motivo) {
        Counter.builder("vote.recusados").tag("motivo", motivo).register(registry).increment();
    }
}
