package ru.practicum.interactionapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.interactionapi.client.api.EventInternalApi;
import ru.practicum.interactionapi.client.config.CommonFeignConfig;
import ru.practicum.interactionapi.client.fallback.EventClientFallbackFactory;

@FeignClient(
        name = "event-service",
        configuration = CommonFeignConfig.class,
        fallbackFactory = EventClientFallbackFactory.class
)
public interface EventClient extends EventInternalApi {
}
