package ru.practicum.event.stats;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.dto.request.EndPointHitDtoNew;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsHitService {

    private final StatsClient statsClient;

    @Value("${ewm.app-name:ewm-event-service}")
    private String appName;

    //отправляет hit в stats-service, если он не доступен, пишем предупрреждение и не даем приложению упасть
    @CircuitBreaker(name = "statsService")
    @Retry(
            name = "statsService",
            fallbackMethod = "sendHitFallback"
    )
    public void sendHit(String uri, String ip) {
        EndPointHitDtoNew hitDto = EndPointHitDtoNew.builder()
                .app(appName)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();
        statsClient.hit(hitDto);
    }
    private void sendHitFallback(String uri, String ip, Throwable exception) {
        log.warn("stats-service недоступен. Хит для uri={} и ip={} не сохранен: {}", uri, ip, exception.getMessage());
    }
}
