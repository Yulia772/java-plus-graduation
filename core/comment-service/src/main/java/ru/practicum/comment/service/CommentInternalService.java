package ru.practicum.comment.service;

import ru.practicum.interactionapi.dto.comment.CommentEventDto;

import java.util.List;

public interface CommentInternalService {
    List<CommentEventDto> findPublishedByEventIds(List<Long> eventIds);
}
