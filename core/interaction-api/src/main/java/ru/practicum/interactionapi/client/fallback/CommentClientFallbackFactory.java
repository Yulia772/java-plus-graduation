package ru.practicum.interactionapi.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.interactionapi.client.CommentClient;
import ru.practicum.interactionapi.exception.ServiceUnavailableException;

import java.util.List;

@Slf4j
@Component
public class CommentClientFallbackFactory
        implements FallbackFactory<CommentClient> {

    @Override
    public CommentClient create(Throwable cause) {
        log.error("Ошибка при обращении к comment-service", cause);

        RuntimeException exception = FallbackExceptionResolver.resolve(
                cause,
                "comment-service"
        );

        return eventIds -> {
            if (exception instanceof ServiceUnavailableException) {
                log.warn(
                        "Comment-service недоступен. " +
                                "Возвращаем события без комментариев, eventIds={}",
                        eventIds
                );

                return List.of();
            }

            throw exception;
        };
    }
}
