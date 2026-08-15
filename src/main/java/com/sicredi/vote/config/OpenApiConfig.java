package com.sicredi.vote.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    OpenAPI voteOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Vote API")
                .version("v1")
                .description("""
                    API de gestao de sessoes de votacao em assembleias de cooperativismo.

                    Fluxo principal: cadastrar uma pauta, abrir uma sessao de votacao, \
                    registrar os votos dos associados e consultar o resultado apurado. \
                    Erros de negocio sao retornados no formato RFC 7807 (application/problem+json).""")
                .contact(new Contact().name("Time Vote").email("vote@sicredi.example"))
                .license(new License().name("Proprietary")))
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Ambiente local")));
    }
}
