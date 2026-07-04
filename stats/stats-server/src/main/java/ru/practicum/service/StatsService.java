package ru.practicum.service;

import ru.practicum.dto.response.EndPointHitDto;
import ru.practicum.dto.request.EndPointHitDtoNew;
import ru.practicum.dto.response.ViewStatsDto;

import java.util.List;

public interface StatsService {

    EndPointHitDto addHit(EndPointHitDtoNew dto);

    List<ViewStatsDto> getStats(String start,
                                String end,
                                List<String> uris,
                                Boolean unique);
}
