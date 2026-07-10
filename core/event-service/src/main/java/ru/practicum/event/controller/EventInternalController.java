package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.event.service.EventInternalService;
import ru.practicum.interactionapi.client.EventClient;
import ru.practicum.interactionapi.dto.request.RequestEventInfo;

@RestController
@RequiredArgsConstructor
public class EventInternalController implements EventClient {

    private final EventInternalService eventInternalService;

    @Override
    public void checkEventExistsAndPublished(Long eventId) {
        eventInternalService.checkEventExistsAndPublished(eventId);
    }

    @Override
    public RequestEventInfo getEventInfo(Long eventId) {
        return eventInternalService.getEventInfo(eventId);
    }
}
