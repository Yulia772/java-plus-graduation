package ru.practicum.analyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EventScore {
    private Long eventId;
    private Double score;
}
