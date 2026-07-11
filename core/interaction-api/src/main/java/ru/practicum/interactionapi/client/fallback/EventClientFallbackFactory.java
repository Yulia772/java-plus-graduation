package ru.practicum.interactionapi.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.interactionapi.client.EventClient;
import ru.practicum.interactionapi.dto.request.RequestEventInfo;

@Slf4j
@Component
public class EventClientFallbackFactory
        implements FallbackFactory<EventClient> {

    @Override
    public EventClient create(Throwable cause) {
        log.error("Ошибка при обращении к event-service", cause);

        RuntimeException exception = FallbackExceptionResolver.resolve(
                cause,
                "event-service"
        );

        return new EventClient() {

            @Override
            public void checkEventExistsAndPublished(Long eventId) {
                throw exception;
            }

            @Override
            public RequestEventInfo getEventInfo(Long eventId) {
                throw exception;
            }
        };
    }
}