package ru.practicum.interactionapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.interactionapi.client.api.RequestInternalApi;
import ru.practicum.interactionapi.client.config.CommonFeignConfig;
import ru.practicum.interactionapi.client.fallback.RequestClientFallbackFactory;

@FeignClient(
        name = "request-service",
        configuration = CommonFeignConfig.class,
        fallbackFactory = RequestClientFallbackFactory.class
)
public interface RequestClient extends RequestInternalApi {
}
