package ru.practicum.interactionapi.client.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.interactionapi.dto.comment.CommentEventDto;

import java.util.List;

public interface CommentInternalApi {

    @GetMapping("/internal/comments/published")
    List<CommentEventDto> findPublishedByEventIds(@RequestParam("eventIds") List<Long> eventIds);
}
