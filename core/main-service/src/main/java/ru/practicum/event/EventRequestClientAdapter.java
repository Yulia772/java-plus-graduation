package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.client.EventRequestClient;
import ru.practicum.interactionapi.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.interactionapi.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.interactionapi.dto.request.ParticipationRequestDto;
import ru.practicum.interactionapi.dto.request.RequestStatus;
import ru.practicum.interactionapi.exception.ConflictException;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EventRequestClientAdapter implements EventRequestClient {

    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    public long confirmedCount(Long eventId) {
        return requestRepository.confirmedCount(eventId);
    }

    @Override
    public Map<Long, Long> confirmedCount(List<Long> eventIds) {
        return requestRepository.confirmedCount(eventIds);
    }

    @Override
    public List<ParticipationRequestDto> findAllByEventId(Long eventId) {
        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::toParticipantRequestDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsStatus(
            Long eventId,
            int participantLimit,
            EventRequestStatusUpdateRequest req
    ) {
        if (req.getStatus() != RequestStatus.CONFIRMED && req.getStatus() != RequestStatus.REJECTED) {
            throw new ConflictException("Статус должен быть CONFIRMED или REJECTED");
        }

        List<Request> requests = requestRepository.findRequestsByIds(req.getRequestIds());

        if (req.getStatus() == RequestStatus.CONFIRMED && participantLimit != 0) {
            long confirmedBefore = requestRepository.confirmedCount(eventId);

            long toConfirm = requests.stream()
                    .filter(r -> r.getStatus() == RequestStatus.PENDING)
                    .count();

            if (confirmedBefore + toConfirm > participantLimit) {
                throw new ConflictException("Превышен лимит участников события");
            }
        }

        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();

        for (Request r : requests) {
            if (r.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Изменить можно только заявки в статусе PENDING");
            }

            if (req.getStatus() == RequestStatus.CONFIRMED) {
                r.setStatus(RequestStatus.CONFIRMED);
                confirmed.add(requestMapper.toParticipantRequestDto(r));
            } else {
                r.setStatus(RequestStatus.REJECTED);
                rejected.add(requestMapper.toParticipantRequestDto(r));
            }
        }

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmed)
                .rejectedRequests(rejected)
                .build();
    }
}


