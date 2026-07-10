package ru.practicum.interactionapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.interactionapi.client.config.CommonFeignConfig;
import ru.practicum.interactionapi.dto.comment.CommentEventDto;

import java.util.List;

@FeignClient(name = "comment-service", configuration = CommonFeignConfig.class)
public interface CommentClient {

    @GetMapping("/internal/comments/published")
    List<CommentEventDto> findPublishedByEventIds(@RequestParam("eventIds") List<Long> eventIds);
}
