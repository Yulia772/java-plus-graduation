package ru.practicum.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
@Getter
@Setter
@ToString
public class ViewStatsParamDto {
    private LocalDateTime start;
    private LocalDateTime end;
    private List<String> uris;
    private Boolean unique;
}
