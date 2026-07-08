package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.common.EntityFinder;
import ru.practicum.event.client.EventUserClient;
import ru.practicum.user.model.User;

@Component
@RequiredArgsConstructor
public class EventUserClientAdapter implements EventUserClient {
    private final EntityFinder entityFinder;

    @Override
    public User getUserOrThrow(Long userId) {
        return entityFinder.getUserOrThrow(userId);
    }
}
