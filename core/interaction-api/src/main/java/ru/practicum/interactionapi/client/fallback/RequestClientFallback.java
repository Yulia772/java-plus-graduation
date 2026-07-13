package ru.practicum.interactionapi.client.fallback;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.practicum.interactionapi.client.RequestClient;
import ru.practicum.interactionapi.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.interactionapi.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.interactionapi.dto.request.ParticipationRequestDto;
import ru.practicum.interactionapi.dto.request.RequestCountDto;

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class RequestClientFallback implements RequestClient {

    private final RuntimeException exception;

    @Override
    public List<ParticipationRequestDto> findAllByEventId(Long eventId) {
        throw exception;
    }

    @Override
    public Long confirmedCount(Long eventId) {
        throw exception;
    }

    @Override
    public List<RequestCountDto> confirmedCounts(List<Long> eventIds) {
        throw exception;
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(
            Long eventId,
            Integer participantLimit,
            EventRequestStatusUpdateRequest req
    ) {
        throw exception;
    }
}
