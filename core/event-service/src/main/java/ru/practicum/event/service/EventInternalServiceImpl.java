package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.interactionapi.dto.event.State;
import ru.practicum.interactionapi.dto.request.RequestEventInfo;
import ru.practicum.interactionapi.exception.ConflictException;
import ru.practicum.interactionapi.exception.NotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventInternalServiceImpl implements EventInternalService {
    private final EventRepository eventRepository;

    @Override
    public void checkEventExistsAndPublished(Long eventId) {
        Event event = getEventOrThrow(eventId);

        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Нельзя написать комментарий на неопубликованное событие");
        }
    }

    @Override
    public RequestEventInfo getEventInfo(Long eventId) {
        Event event = getEventOrThrow(eventId);

        return new RequestEventInfo(
                event.getId(),
                event.getInitiatorId(),
                event.getState(),
                event.getPartLimit(),
                event.isRequestModeration()
        );
    }

    private Event getEventOrThrow(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id=" + eventId + " не найдено"));
    }
}
