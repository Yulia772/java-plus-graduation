package ru.practicum.event.client;

import ru.practicum.interactionapi.dto.comment.CommentEventDto;

import java.util.List;
import java.util.Map;

public interface EventCommentClient {
    List<CommentEventDto> findPublishedByEventId(Long eventId);

    Map<Long, List<CommentEventDto>> findPublishedByEventIds(List<Long> eventIds);
}
