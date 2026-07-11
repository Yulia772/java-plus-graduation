package ru.practicum.interactionapi.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.interactionapi.client.RequestClient;
import ru.practicum.interactionapi.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.interactionapi.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.interactionapi.dto.request.ParticipationRequestDto;
import ru.practicum.interactionapi.dto.request.RequestCountDto;

import java.util.List;

@Slf4j
@Component
public class RequestClientFallbackFactory
        implements FallbackFactory<RequestClient> {

    @Override
    public RequestClient create(Throwable cause) {
        log.error("Ошибка при обращении к request-service", cause);

        RuntimeException exception = FallbackExceptionResolver.resolve(
                cause,
                "request-service"
        );

        return new RequestClient() {

            @Override
            public List<ParticipationRequestDto> findAllByEventId(
                    Long eventId
            ) {
                throw exception;
            }

            @Override
            public Long confirmedCount(Long eventId) {
                throw exception;
            }

            @Override
            public List<RequestCountDto> confirmedCounts(
                    List<Long> eventIds
            ) {
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
        };
    }
}
