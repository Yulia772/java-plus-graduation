package ru.practicum.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.event.model.Event;
import ru.practicum.request.client.RequestEventClient;
import ru.practicum.request.client.RequestEventInfo;
import ru.practicum.request.client.RequestUserClient;

@Component
@RequiredArgsConstructor
public class RequestClientAdapter implements RequestUserClient, RequestEventClient  {
    private final EntityFinder entityFinder;

    @Override
    public void checkUserExists(Long userId) {
        entityFinder.getUserOrThrow(userId);
    }

    @Override
    public RequestEventInfo getEventInfo(Long eventId) {
        Event event = entityFinder.getEventOrThrow(eventId);
        return new RequestEventInfo(
                event.getId(),
                event.getInitiator().getId(),
                event.getState(),
                event.getPartLimit(),
                event.isRequestModeration()
        );
    }
}
