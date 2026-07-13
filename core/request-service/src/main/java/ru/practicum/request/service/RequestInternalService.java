package ru.practicum.request.service;

import org.springframework.web.bind.annotation.*;
import ru.practicum.interactionapi.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.interactionapi.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.interactionapi.dto.request.ParticipationRequestDto;
import ru.practicum.interactionapi.dto.request.RequestCountDto;

import java.util.List;

public interface RequestInternalService {

    List<ParticipationRequestDto> findAllByEventId(Long eventId);

    Long confirmedCount(Long eventId);

    List<RequestCountDto> confirmedCounts(List<Long> eventIds);

    EventRequestStatusUpdateResult updateRequestsStatus(
            Long eventId,
            Integer participantLimit,
            EventRequestStatusUpdateRequest req
    );
}
