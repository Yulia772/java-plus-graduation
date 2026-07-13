package ru.practicum.interactionapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.interactionapi.client.api.CommentInternalApi;
import ru.practicum.interactionapi.client.config.CommonFeignConfig;
import ru.practicum.interactionapi.client.fallback.CommentClientFallbackFactory;

@FeignClient(
        name = "comment-service",
        configuration = CommonFeignConfig.class,
        fallbackFactory = CommentClientFallbackFactory.class
)
public interface CommentClient extends CommentInternalApi {
}
