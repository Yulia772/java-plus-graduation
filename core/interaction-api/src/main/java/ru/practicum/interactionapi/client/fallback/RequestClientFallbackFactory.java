package ru.practicum.interactionapi.client.fallback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.interactionapi.client.RequestClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestClientFallbackFactory
        implements FallbackFactory<RequestClient> {

    private final ObjectProvider<RequestClientFallback> fallbackProvider;

    @Override
    public RequestClient create(Throwable cause) {
        log.error("Ошибка при обращении к request-service", cause);

        RuntimeException exception = FallbackExceptionResolver.resolve(
                cause,
                "request-service"
        );

        return fallbackProvider.getObject(exception);
    }
}
