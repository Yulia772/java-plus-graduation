package ru.practicum.event.client;

import ru.practicum.interactionapi.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.interactionapi.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.interactionapi.dto.request.ParticipationRequestDto;

import java.util.List;
import java.util.Map;

public interface EventRequestClient {

    long confirmedCount(Long eventId);

    Map<Long, Long> confirmedCount(List<Long> eventIds);

    List<ParticipationRequestDto> findAllByEventId(Long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(
            Long eventId,
            int participantLimit,
            EventRequestStatusUpdateRequest request
    );
}
