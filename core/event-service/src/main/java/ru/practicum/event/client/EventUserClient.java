package ru.practicum.event.client;

import ru.practicum.user.model.User;

public interface EventUserClient {
    User getUserOrThrow(Long userId);
}
