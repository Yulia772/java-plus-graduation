package ru.practicum.interactionapi.client.fallback;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.practicum.interactionapi.client.EventClient;
import ru.practicum.interactionapi.dto.request.RequestEventInfo;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class EventClientFallback implements EventClient {

    private final RuntimeException exception;

    @Override
    public void checkEventExistsAndPublished(Long eventId) {
        throw exception;
    }

    @Override
    public RequestEventInfo getEventInfo(Long eventId) {
        throw exception;
    }
}
