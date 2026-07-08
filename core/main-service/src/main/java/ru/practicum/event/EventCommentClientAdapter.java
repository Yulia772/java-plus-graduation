package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.client.EventCommentClient;
import ru.practicum.interactionapi.dto.comment.CommentEventDto;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventCommentClientAdapter implements EventCommentClient {

    private final CommentRepository commentRepository;
    public final CommentMapper commentMapper;

    @Override
    public List<CommentEventDto> findPublishedByEventId(Long eventId) {
        return findPublishedByEventIds(List.of(eventId))
                .getOrDefault(eventId, Collections.emptyList());
    }

    @Override
    public Map<Long, List<CommentEventDto>> findPublishedByEventIds(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<Comment> comments = commentRepository.findPublishedByEventIds(eventIds);

        return comments.stream()
                .map(commentMapper::toCommentEventDto)
                .collect(Collectors.groupingBy(CommentEventDto::getEventId));
    }
}