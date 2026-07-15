package ru.practicum.analyzer.service;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

public interface StatsStorageService {
    void saveUserAction(UserActionAvro action);

    void saveEventSimilarity(EventSimilarityAvro similarity);
}
