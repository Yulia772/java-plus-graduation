package ru.practicum.interactionapi.client.fallback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.interactionapi.client.CommentClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentClientFallbackFactory
        implements FallbackFactory<CommentClient> {

    private final ObjectProvider<CommentClientFallback> fallbackProvider;

    @Override
    public CommentClient create(Throwable cause) {
        log.error("Ошибка при обращении к comment-service", cause);

        RuntimeException exception = FallbackExceptionResolver.resolve(
                cause,
                "comment-service"
        );

        return fallbackProvider.getObject(exception);
    }
}
