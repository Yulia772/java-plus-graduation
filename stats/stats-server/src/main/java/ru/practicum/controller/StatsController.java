package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.request.EndPointHitDtoNew;
import ru.practicum.dto.response.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {

    private  final StatsService statsService;

    @PostMapping("hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void createHit(@Valid @RequestBody EndPointHitDtoNew dto) {
        statsService.addHit(dto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam @NotNull String start,
                                       @RequestParam @NotNull String end,
                                       @RequestParam(required = false) List<String> uris,
                                       @RequestParam(defaultValue = "false") Boolean unique) {
        return statsService.getStats(start, end, uris, unique);
    }
}
