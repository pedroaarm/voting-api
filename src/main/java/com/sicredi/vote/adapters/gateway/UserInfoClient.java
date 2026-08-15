package com.sicredi.vote.adapters.gateway;

import com.sicredi.vote.adapters.gateway.dto.UserInfoResponse;
import com.sicredi.vote.application.exception.ElegibilidadeIndisponivelException;
import com.sicredi.vote.application.port.out.Elegibilidade;
import com.sicredi.vote.application.port.out.VerificadorElegibilidade;
import com.sicredi.vote.config.ElegibilidadeProperties;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import java.util.function.Supplier;

@Component
@ConditionalOnProperty(name = "vote.elegibilidade.provider", havingValue = "user-info")
public class UserInfoClient implements VerificadorElegibilidade {

    private static final Logger log = LoggerFactory.getLogger(UserInfoClient.class);

    private final RestClient client;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;

    public UserInfoClient(ElegibilidadeProperties props,
                          CircuitBreakerRegistry cbRegistry, RetryRegistry retryRegistry) {
        SimpleClientHttpRequestFactory rf = new SimpleClientHttpRequestFactory();
        rf.setConnectTimeout(props.timeout());
        rf.setReadTimeout(props.timeout());
        this.client = RestClient.builder().baseUrl(props.url()).requestFactory(rf).build();
        this.circuitBreaker = cbRegistry.circuitBreaker("userInfo");
        this.retry = retryRegistry.retry("userInfo");
    }

    @Override
    public Elegibilidade verificar(String cpf) {
        Supplier<Elegibilidade> chamada = () -> chamar(cpf);
        Supplier<Elegibilidade> resiliente =
            Retry.decorateSupplier(retry, CircuitBreaker.decorateSupplier(circuitBreaker, chamada));
        try {
            return resiliente.get();
        } catch (CallNotPermittedException e) {
            log.warn("Elegibilidade indisponivel: circuito aberto para o servico userInfo");
            throw new ElegibilidadeIndisponivelException("circuito aberto para o servico de elegibilidade", e);
        }
    }

    private Elegibilidade chamar(String cpf) {
        try {
            UserInfoResponse body = client.get().uri("/users/{cpf}", cpf)
                .retrieve().body(UserInfoResponse.class);
            if (body == null || body.status() == null) {
                throw new ElegibilidadeIndisponivelException("resposta invalida do servico de elegibilidade");
            }
            return Elegibilidade.valueOf(body.status());
        } catch (HttpClientErrorException.NotFound e) {
            return Elegibilidade.UNABLE_TO_VOTE;
        } catch (ElegibilidadeIndisponivelException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Falha ao consultar o servico de elegibilidade: {}", e.toString());
            throw new ElegibilidadeIndisponivelException("falha ao consultar o servico de elegibilidade", e);
        }
    }
}
