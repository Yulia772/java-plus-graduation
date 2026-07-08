package ru.practicum.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.comment.client.CommentEventClient;
import ru.practicum.comment.client.CommentUserClient;
import ru.practicum.event.model.Event;
import ru.practicum.interactionapi.dto.event.State;
import ru.practicum.interactionapi.exception.ConflictException;
import ru.practicum.request.client.RequestEventInfo;

@Component
@RequiredArgsConstructor
public class CommentClientAdapter implements CommentEventClient, CommentUserClient {
    private final EntityFinder entityFinder;

    @Override
    public void checkUserExists(Long userId) {
        entityFinder.getUserOrThrow(userId);
    }

    @Override
    public void checkEventExistsAndPublished(Long eventId) {
        Event event = entityFinder.getEventOrThrow(eventId);
        if (event.getState() != State.PUBLISHED) {
            throw new ConflictException("Нельзя написать комментарий на неопубликованное событие");
        }
    }
}
