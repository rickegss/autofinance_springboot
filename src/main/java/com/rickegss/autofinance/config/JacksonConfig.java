package com.rickegss.autofinance.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {

   @Bean
    public ObjectMapper objectMapper() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.modules(new JavaTimeModule());
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        builder.timeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
        return builder.build();
    }
}
