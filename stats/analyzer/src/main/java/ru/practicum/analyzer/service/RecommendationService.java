package ru.practicum.analyzer.service;

import ru.practicum.analyzer.dto.EventScore;

import java.util.List;

public interface RecommendationService {
    List<EventScore> getRecommendationsForUser(Long userId, int maxResults);

    List<EventScore> getSimilarEvents(Long eventId, Long userId, int maxResults);

    List<EventScore> getInteractionsCount(List<Long> eventIds);
}
