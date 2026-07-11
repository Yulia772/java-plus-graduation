package ru.practicum.interactionapi.client.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.interactionapi.dto.request.RequestEventInfo;

public interface EventInternalApi {
    @GetMapping("/internal/events/{eventId}/published")
    void checkEventExistsAndPublished(@PathVariable Long eventId);

    @GetMapping("/internal/events/{eventId}/request-info")
    RequestEventInfo getEventInfo(@PathVariable Long eventId);
}
