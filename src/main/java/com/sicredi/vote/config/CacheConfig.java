package com.sicredi.vote.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.time.Duration;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

@Configuration
@EnableCaching
public class CacheConfig {

  public static final String CACHE_ELEGIBILIDADE = "elegibilidade";

  public static final String CACHE_PAUTA = "pauta";

  public static final String CACHE_PAUTAS_LISTA = "pautasLista";

  public static final String CACHE_RESULTADO = "resultado";

  @Bean
  RedisCacheManagerBuilderCustomizer cacheCustomizer() {
    SerializationPair<Object> json =
        SerializationPair.fromSerializer(
            new GenericJackson2JsonRedisSerializer(cacheObjectMapper()));
    return builder ->
        builder
            .withCacheConfiguration(CACHE_ELEGIBILIDADE, config(Duration.ofMinutes(5), json))
            .withCacheConfiguration(CACHE_PAUTA, config(Duration.ofHours(1), json))
            .withCacheConfiguration(CACHE_PAUTAS_LISTA, config(Duration.ofMinutes(10), json))
            .withCacheConfiguration(CACHE_RESULTADO, config(Duration.ofHours(1), json));
  }

  private RedisCacheConfiguration config(Duration ttl, SerializationPair<Object> values) {
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(ttl)
        .disableCachingNullValues()
        .serializeValuesWith(values);
  }

  private ObjectMapper cacheObjectMapper() {
    PolymorphicTypeValidator ptv =
        BasicPolymorphicTypeValidator.builder()
            .allowIfSubType("com.sicredi.vote.")
            .allowIfSubType("java.util.")
            .allowIfSubType("java.time.")
            .allowIfSubType("java.lang.")
            .build();
    ObjectMapper mapper =
        JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .addModule(new ParameterNamesModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .activateDefaultTyping(
                ptv, ObjectMapper.DefaultTyping.EVERYTHING, JsonTypeInfo.As.PROPERTY)
            .build();
    mapper.setVisibility(
        mapper
            .getSerializationConfig()
            .getDefaultVisibilityChecker()
            .withCreatorVisibility(JsonAutoDetect.Visibility.ANY)
            .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
            .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
            .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));
    return mapper;
  }
}
