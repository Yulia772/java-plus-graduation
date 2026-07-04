package ru.practicum.dto.response;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для представления статистических данных по хитам.
 * Содержит агрегированную информацию: приложение, URI и количество хитов.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class ViewStatsDto {
    private String app;

    private String uri;

    @PositiveOrZero(message = "Количество хитов не может быть отрицательным.")
    private Long hits;

    public ViewStatsDto(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}
