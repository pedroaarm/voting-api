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

  /** Elegibilidade por CPF (chamada externa). TTL curto: o status pode mudar. */
  public static final String CACHE_ELEGIBILIDADE = "elegibilidade";

  /** Pauta por id. Pautas sao imutaveis apos criadas -> TTL longo. */
  public static final String CACHE_PAUTA = "pauta";

  /** Lista de pautas. Invalidada a cada nova pauta (@CacheEvict no salvar). */
  public static final String CACHE_PAUTAS_LISTA = "pautasLista";

  @Bean
  RedisCacheManagerBuilderCustomizer cacheCustomizer() {
    SerializationPair<Object> json =
        SerializationPair.fromSerializer(
            new GenericJackson2JsonRedisSerializer(cacheObjectMapper()));
    return builder ->
        builder
            .withCacheConfiguration(CACHE_ELEGIBILIDADE, config(Duration.ofMinutes(5), json))
            .withCacheConfiguration(CACHE_PAUTA, config(Duration.ofHours(1), json))
            .withCacheConfiguration(CACHE_PAUTAS_LISTA, config(Duration.ofMinutes(10), json));
  }

  private RedisCacheConfiguration config(Duration ttl, SerializationPair<Object> values) {
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(ttl)
        .disableCachingNullValues()
        .serializeValuesWith(values);
  }

  /**
   * ObjectMapper dedicado ao cache. Guarda o tipo concreto (@class) para round-trip de enums e dos
   * modelos de dominio, que sao imutaveis (campos final + @Builder do Lombok, sem construtor
   * publico). ParameterNamesModule + visibilidade de creator permitem desserializar pelo construtor
   * gerado, mantendo o dominio livre de anotacoes de framework.
   */
  private ObjectMapper cacheObjectMapper() {
    PolymorphicTypeValidator ptv =
        BasicPolymorphicTypeValidator.builder().allowIfSubType(Object.class).build();
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
            .withFieldVisibility(JsonAutoDetect.Visibility.ANY));
    return mapper;
  }
}
