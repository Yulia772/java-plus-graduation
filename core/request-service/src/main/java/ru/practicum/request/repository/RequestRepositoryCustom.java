package ru.practicum.request.repository;

import ru.practicum.request.model.Request;
import ru.practicum.interactionapi.dto.request.RequestStatus;

import java.util.List;
import java.util.Map;

public interface RequestRepositoryCustom {
    long confirmedCount(Long eventId);

    Map<Long, Long> confirmedCount(List<Long> eventIds);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findRequestsByIds(List<Long> ids); // Новый метод

    List<Request> findAllByRequesterId(Long requesterId);

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findRequestsByStatusAndEvent(RequestStatus status, Long eventId);
}