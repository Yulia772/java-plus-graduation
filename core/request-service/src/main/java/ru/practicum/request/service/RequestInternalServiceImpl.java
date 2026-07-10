package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interactionapi.dto.event.EventRequestStatusUpdateRequest;
import ru.practicum.interactionapi.dto.event.EventRequestStatusUpdateResult;
import ru.practicum.interactionapi.dto.request.ParticipationRequestDto;
import ru.practicum.interactionapi.dto.request.RequestCountDto;
import ru.practicum.interactionapi.dto.request.RequestStatus;
import ru.practicum.interactionapi.exception.ConflictException;
import ru.practicum.interactionapi.exception.NotFoundException;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestInternalServiceImpl implements RequestInternalService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;

    @Override
    public Long confirmedCount(Long eventId) {
        return requestRepository.confirmedCount(eventId);
    }

    @Override
    public List<RequestCountDto> confirmedCounts(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return List.of();
        }
        Map<Long, Long> counts = requestRepository.confirmedCount(eventIds);
        return counts.entrySet().stream()
                .map(entry -> new RequestCountDto(entry.getKey(), entry.getValue()))
                .toList();
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
            Integer participantLimit,
            EventRequestStatusUpdateRequest req
    ) {
        if (req.getStatus() != RequestStatus.CONFIRMED && req.getStatus() != RequestStatus.REJECTED) {
            throw new ConflictException("Статус должен быть CONFIRMED или REJECTED");
        }

        List<Request> requests = requestRepository.findRequestsByIds(req.getRequestIds());
        if (requests.size() != req.getRequestIds().size()) {
            throw new NotFoundException("В списке есть несуществующие заявки");
        }
        boolean hasAnotherRequests = requests.stream()
                .anyMatch(r -> !r.getEventId().equals(eventId));
        if (hasAnotherRequests) {
            throw new ConflictException("Все заявки должны относиться к событию id=" + eventId);
        }

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
