package ru.practicum.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для полного представления хита конечной точки (с ID).
 * Используется для ответов API с полной информацией.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class EndPointHitDto {

    private Long id;

    private String app;

    private String uri;

    private String ip;

    private LocalDateTime timestamp;
}
