package ru.practicum.interactionapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.interactionapi.client.api.UserInternalApi;
import ru.practicum.interactionapi.client.config.CommonFeignConfig;
import ru.practicum.interactionapi.client.fallback.UserClientFallbackFactory;

@FeignClient(
        name = "user-service",
        configuration = CommonFeignConfig.class,
        fallbackFactory = UserClientFallbackFactory.class
)
public interface UserClient extends UserInternalApi {
}
