package com.sicredi.vote.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "vote.elegibilidade")
public record ElegibilidadeProperties(
    @DefaultValue("sempre") String provider,
    @DefaultValue("https://user-info.herokuapp.com") String url,
    @DefaultValue("2s") Duration timeout) {}
