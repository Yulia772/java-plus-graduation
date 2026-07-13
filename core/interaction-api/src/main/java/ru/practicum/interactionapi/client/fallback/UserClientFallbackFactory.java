package ru.practicum.interactionapi.client.fallback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.interactionapi.client.UserClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserClientFallbackFactory
        implements FallbackFactory<UserClient> {

    private final ObjectProvider<UserClientFallback> fallbackProvider;

    @Override
    public UserClient create(Throwable cause) {
        log.error("Ошибка при обращении к user-service", cause);

        RuntimeException exception = FallbackExceptionResolver.resolve(
                cause,
                "user-service"
        );

        return fallbackProvider.getObject(exception);
    }
}

