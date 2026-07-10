package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.interactionapi.client.RequestClient;
import ru.practicum.interactionapi.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.interactionapi.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.interactionapi.dto.request.ParticipationRequestDto;
import ru.practicum.interactionapi.dto.request.RequestCountDto;
import ru.practicum.request.service.RequestInternalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RequestInternalController implements RequestClient {
    private final RequestInternalService requestInternalService;

    @Override
    public List<ParticipationRequestDto> findAllByEventId(Long eventId) {
        return requestInternalService.findAllByEventId(eventId);
    }

    @Override
    public Long confirmedCount(Long eventId) {
        return requestInternalService.confirmedCount(eventId);
    }

    @Override
    public List<RequestCountDto> confirmedCounts(List<Long> eventIds) {
        return requestInternalService.confirmedCounts(eventIds);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(
            Long eventId,
            Integer participantLimit,
            EventRequestStatusUpdateRequest req
    ) {
        return requestInternalService.updateRequestsStatus(eventId, participantLimit, req);
    }
}
