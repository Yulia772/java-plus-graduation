package ru.practicum.event.service;

import ru.practicum.interactionapi.dto.request.RequestEventInfo;

public interface EventInternalService {
    void checkEventExistsAndPublished(Long eventId);

    RequestEventInfo getEventInfo(Long eventId);
}
