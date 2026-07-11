package ru.practicum.interactionapi.client.fallback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.practicum.interactionapi.client.CommentClient;
import ru.practicum.interactionapi.dto.comment.CommentEventDto;
import ru.practicum.interactionapi.exception.ServiceUnavailableException;

import java.util.List;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class CommentClientFallback implements CommentClient {

    private final RuntimeException exception;

    @Override
    public List<CommentEventDto> findPublishedByEventIds(List<Long> eventIds) {
        if (exception instanceof ServiceUnavailableException) {
            log.warn("Comment-service недоступен. " +
                    "Возвращаем пустой список комментариев для eventIds={}", eventIds);
            return List.of();
        }
        throw exception;
    }
}
