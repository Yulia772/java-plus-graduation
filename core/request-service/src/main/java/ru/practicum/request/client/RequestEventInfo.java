package ru.practicum.request.client;

import ru.practicum.interactionapi.dto.event.State;

public record RequestEventInfo(
    Long id,
    Long initiatorId,
    State state,
    Integer partLimit,
    Boolean requestModeration
) {}
