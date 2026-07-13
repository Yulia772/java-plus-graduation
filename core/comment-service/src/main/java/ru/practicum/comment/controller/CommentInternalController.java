package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.service.CommentInternalService;
import ru.practicum.interactionapi.client.CommentClient;
import ru.practicum.interactionapi.dto.comment.CommentEventDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentInternalController implements CommentClient {
    private final CommentInternalService commentInternalService;

    @Override
    public List<CommentEventDto> findPublishedByEventIds(List<Long> eventIds) {
        return commentInternalService.findPublishedByEventIds(eventIds);
    }
}
