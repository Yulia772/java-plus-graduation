package ru.practicum.interactionapi.client.api;

import org.springframework.web.bind.annotation.*;
import ru.practicum.interactionapi.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.interactionapi.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.interactionapi.dto.request.ParticipationRequestDto;
import ru.practicum.interactionapi.dto.request.RequestCountDto;

import java.util.List;

public interface RequestInternalApi {

    @GetMapping("/internal/requests/events/{eventId}")
    List<ParticipationRequestDto> findAllByEventId(@PathVariable("eventId") Long eventId);

    @GetMapping("/internal/requests/events/{eventId}/confirmed-count")
    Long confirmedCount(@PathVariable("eventId") Long eventId);

    @GetMapping("/internal/requests/confirmed-counts")
    List<RequestCountDto> confirmedCounts(@RequestParam("eventIds") List<Long> eventIds);

    @PostMapping("/internal/requests/events/{eventId}/status")
    EventRequestStatusUpdateResult updateRequestsStatus(
            @PathVariable("eventId") Long eventId,
            @RequestParam("participantLimit") Integer participantLimit,
            @RequestBody EventRequestStatusUpdateRequest req
    );
}
