package ru.practicum.interactionapi.dto.comment;

import lombok.Builder;
import lombok.Data;
import ru.practicum.interactionapi.dto.event.State;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder(toBuilder = true)
public class AdminCommentFilterParams {
    private Set<Long> comments;
    private Set<Long> authors;
    private Set<Long> events;
    private Set<State> states;
    private String text;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
}
