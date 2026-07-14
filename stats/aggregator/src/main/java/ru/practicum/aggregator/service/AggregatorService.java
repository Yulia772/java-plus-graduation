package ru.practicum.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AggregatorService {
    //матрица максимальных весов: eventId->userId->maxWeight
    private final Map<Long, Map<Long, Double>> eventUserWeights = new HashMap<>();
    //Сумма весов по каждому мероприятию: eventId->sumWeight
    private final Map<Long, Double> eventWeightSums = new HashMap<>();
    //Сумма минимальных весов по паре мероприятий: min(eventA, eventB)->max(eventA, eventB)->Smin
    private final Map<Long, Map<Long, Double>> minWeightsSum = new HashMap<>();

    public synchronized List<EventSimilarityAvro> process(UserActionAvro action) {
        long userId = action.getUserId();
        long eventId = action.getEventId();
        double newWeight = getWeight(action.getActionType());

        Map<Long, Double> userWeights = eventUserWeights.computeIfAbsent(eventId, id -> new HashMap<>());
        double oldWeight = userWeights.getOrDefault(userId, 0.0);

        if (newWeight <= oldWeight) {
            log.info("Вес не увеличился, пересчет не нужен: userId={}, eventId={}, oldWeight={}, newWeight={}",
                    userId, eventId, oldWeight, newWeight);
            return List.of();
        }
        double weightDelta = newWeight - oldWeight;

        userWeights.put(userId, newWeight);
        eventWeightSums.merge(eventId, weightDelta, Double::sum);

        List<EventSimilarityAvro> similarities = new ArrayList<>();

        for (Long otherEventId : eventUserWeights.keySet()) {
            if (otherEventId.equals(eventId)) {
                continue;
            }

            EventSimilarityAvro similarity = updateSimilarity(
                    eventId,
                    otherEventId,
                    userId,
                    oldWeight,
                    newWeight,
                    action
            );

            if (similarity != null) {
                similarities.add(similarity);
            }
        }
        return similarities;
    }

    private EventSimilarityAvro updateSimilarity(
            long eventId,
            long otherEventId,
            long userId,
            double oldWeight,
            double newWeight,
            UserActionAvro action
    ) {
        double otherWeight = eventUserWeights
                .getOrDefault(otherEventId, Map.of())
                .getOrDefault(userId, 0.0);
        if (otherWeight == 0.0) {
            return null;
        }
        double oldMin = Math.min(oldWeight, otherWeight);
        double newMin = Math.min(newWeight, otherWeight);
        double minDelta = newMin - oldMin;

        double newMinSum = getMinWeightSum(eventId, otherEventId) + minDelta;

        if (minDelta != 0.0) {
            putMinWeightSum(eventId, otherEventId, newMinSum);
        }
        if (newMinSum == 0.0) {
            return null;
        }

        double eventSum = eventWeightSums.getOrDefault(eventId, 0.0);
        double otherEventSum = eventWeightSums.getOrDefault(otherEventId, 0.0);

        if (eventSum == 0.0 || otherEventSum == 0.0) {
            return null;
        }

        double score = newMinSum / Math.sqrt(eventSum * otherEventSum);

        long first = Math.min(eventId, otherEventId);
        long second = Math.max(eventId, otherEventId);

        log.info("Пересчитано сходство: eventA={}, eventB={}, score={}", first, second, score);

        return EventSimilarityAvro.newBuilder()
                .setEventA(first)
                .setEventB(second)
                .setScore(score)
                .setTimestamp(action.getTimestamp())
                .build();
    }

    private double getWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
        };
    }

    private double getMinWeightSum(long eventA, long eventB) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        return minWeightsSum
                .getOrDefault(first, Map.of())
                .getOrDefault(second, 0.0);
    }

    private void putMinWeightSum(long eventA, long eventB, double sum) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        minWeightsSum
                .computeIfAbsent(first, id -> new HashMap<>())
                .put(second, sum);
    }
}
