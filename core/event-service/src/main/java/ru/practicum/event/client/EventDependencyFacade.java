package ru.practicum.event.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.dto.request.ViewStatsParamDto;
import ru.practicum.dto.response.ViewStatsDto;
import ru.practicum.interactionapi.client.CommentClient;
import ru.practicum.interactionapi.client.RequestClient;
import ru.practicum.interactionapi.client.UserClient;
import ru.practicum.interactionapi.dto.comment.CommentEventDto;
import ru.practicum.interactionapi.dto.request.RequestCountDto;
import ru.practicum.interactionapi.dto.user.UserShortDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventDependencyFacade {

    private static final String UNAVAILABLE_USER_NAME = "Пользователь недоступен";

    private final RequestClient requestClient;
    private final CommentClient commentClient;
    private final UserClient userClient;
    private final StatsClient statsClient;

    @CircuitBreaker(name = "requestService")
    @Retry(
            name = "requestService",
            fallbackMethod = "confirmedCountFallback"
    )
    public Long confirmedCount(Long eventId) {
        return requestClient.confirmedCount(eventId);
    }

    @CircuitBreaker(name = "requestService")
    @Retry(
            name = "requestService",
            fallbackMethod = "confirmedCountsFallback"
    )
    public List<RequestCountDto> confirmedCounts(List<Long> eventIds) {
        return requestClient.confirmedCounts(eventIds);
    }

    @CircuitBreaker(name = "commentService")
    @Retry(
            name = "commentService",
            fallbackMethod = "publishedCommentsFallback"
    )
    public List<CommentEventDto> publishedComments(List<Long> eventIds) {
        return commentClient.findPublishedByEventIds(eventIds);
    }

    @CircuitBreaker(name = "userService")
    @Retry(
            name = "userService",
            fallbackMethod = "userFallback"
    )
    public UserShortDto user(Long userId) {
        return userClient.getUserShortDto(userId);
    }

    @CircuitBreaker(name = "userService")
    @Retry(
            name = "userService",
            fallbackMethod = "usersFallback"
    )
    public List<UserShortDto> users(List<Long> userIds) {
        return userClient.getUserShortDtos(userIds);
    }

    @CircuitBreaker(name = "statsService")
    @Retry(
            name = "statsService",
            fallbackMethod = "statsFallback"
    )
    public List<ViewStatsDto> stats(ViewStatsParamDto params) {
        return statsClient.get(params);
    }

    private Long confirmedCountFallback(Long eventId, Throwable exception) {
        log.warn(
                "request-service недоступен. Для eventId={} возвращаю confirmedRequests=0: {}",
                eventId,
                exception.getMessage()
        );

        return 0L;
    }

    private List<RequestCountDto> confirmedCountsFallback(
            List<Long> eventIds,
            Throwable exception
    ) {
        log.warn(
                "request-service недоступен. Для событий {} возвращаю confirmedRequests=0: {}",
                eventIds,
                exception.getMessage()
        );

        return List.of();
    }

    private List<CommentEventDto> publishedCommentsFallback(
            List<Long> eventIds,
            Throwable exception
    ) {
        log.warn(
                "comment-service недоступен. Для событий {} возвращаю пустой список комментариев: {}",
                eventIds,
                exception.getMessage()
        );

        return List.of();
    }

    private UserShortDto userFallback(Long userId, Throwable exception) {
        log.warn(
                "user-service недоступен. Для userId={} возвращаю заглушку: {}",
                userId,
                exception.getMessage()
        );

        return unavailableUser(userId);
    }

    private List<UserShortDto> usersFallback(
            List<Long> userIds,
            Throwable exception
    ) {
        log.warn(
                "user-service недоступен. Для пользователей {} возвращаю заглушки: {}",
                userIds,
                exception.getMessage()
        );

        return userIds.stream()
                .map(this::unavailableUser)
                .toList();
    }

    private List<ViewStatsDto> statsFallback(
            ViewStatsParamDto params,
            Throwable exception
    ) {
        log.warn(
                "stats-service недоступен. Возвращаю пустую статистику: {}",
                exception.getMessage()
        );

        return List.of();
    }

    private UserShortDto unavailableUser(Long userId) {
        return UserShortDto.builder()
                .id(userId)
                .name(UNAVAILABLE_USER_NAME)
                .build();
    }
}