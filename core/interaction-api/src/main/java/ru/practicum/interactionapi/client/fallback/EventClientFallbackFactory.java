package ru.practicum.interactionapi.client.fallback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.interactionapi.client.EventClient;

@Slf4j
@Component@RequiredArgsConstructor
public class EventClientFallbackFactory
        implements FallbackFactory<EventClient> {

    private final ObjectProvider<EventClientFallback> fallbackProvider;

    @Override
    public EventClient create(Throwable cause) {
        log.error("Ошибка при обращении к event-service", cause);

        RuntimeException exception = FallbackExceptionResolver.resolve(
                cause,
                "event-service"
        );

        return fallbackProvider.getObject(exception);
    }
}