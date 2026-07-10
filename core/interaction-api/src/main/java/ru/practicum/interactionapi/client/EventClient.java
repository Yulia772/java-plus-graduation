package ru.practicum.interactionapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.interactionapi.client.config.CommonFeignConfig;
import ru.practicum.interactionapi.dto.request.RequestEventInfo;

@FeignClient(
        name = "event-service",
        contextId = "eventClient",
        configuration = CommonFeignConfig.class
)
public interface EventClient {
    @GetMapping("internal/events/{eventId}/published")
    void checkEventExistsAndPublished(@PathVariable Long eventId);

    @GetMapping("/internal/events/{eventId}/request-info")
    RequestEventInfo getEventInfo(@PathVariable Long eventId);
}
