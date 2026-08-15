package com.sicredi.vote.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "vote.callback")
public record CallbackProperties(@DefaultValue("http://localhost:8080") String baseUrl) {}
