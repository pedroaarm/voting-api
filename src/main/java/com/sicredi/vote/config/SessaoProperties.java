package com.sicredi.vote.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "vote.sessao")
public record SessaoProperties(@DefaultValue("1") int duracaoDefaultMinutos) {}
