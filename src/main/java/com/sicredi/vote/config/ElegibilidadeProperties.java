package com.sicredi.vote.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import java.time.Duration;

@ConfigurationProperties(prefix = "vote.elegibilidade")
public record ElegibilidadeProperties(
    @DefaultValue("sempre") String provider,
    @DefaultValue("https://user-info.herokuapp.com") String url,
    @DefaultValue("2s") Duration timeout
) {}
