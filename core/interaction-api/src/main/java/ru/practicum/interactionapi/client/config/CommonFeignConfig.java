package ru.practicum.interactionapi.client.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class CommonFeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CommonFeignErrorDecoder();
    }
}
