package ru.practicum.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AggregatorService {

    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;
    private static final BigDecimal VIEW_WEIGHT = BigDecimal.valueOf(0.4);
    private static final BigDecimal REGISTER_WEIGHT = BigDecimal.valueOf(0.8);
    private static final BigDecimal LIKE_WEIGHT = BigDecimal.valueOf(1.0);

    //матрица максимальных весов: eventId->userId->maxWeight
    private final Map<Long, Map<Long, BigDecimal>> eventUserWeights = new HashMap<>();
    //Сумма весов по каждому мероприятию: eventId->sumWeight
    private final Map<Long, BigDecimal> eventWeightSums = new HashMap<>();
    //Сумма минимальных весов по паре мероприятий: min(eventA, eventB)->max(eventA, eventB)->Smin
    private final Map<Long, Map<Long, BigDecimal>> minWeightsSum = new HashMap<>();

    public synchronized List<EventSimilarityAvro> process(UserActionAvro action) {
        long userId = action.getUserId();
        long eventId = action.getEventId();
        BigDecimal newWeight = getWeight(action.getActionType());

        Map<Long, BigDecimal> userWeights = eventUserWeights.computeIfAbsent(eventId, id -> new HashMap<>());
        BigDecimal oldWeight = userWeights.getOrDefault(userId, BigDecimal.ZERO);

        if (newWeight.compareTo(oldWeight) <= 0) {
            log.info("Вес не увеличился, пересчет не нужен: userId={}, eventId={}, oldWeight={}, newWeight={}",
                    userId, eventId, oldWeight, newWeight);
            return List.of();
        }
        BigDecimal weightDelta = newWeight.subtract(oldWeight, MATH_CONTEXT);

        userWeights.put(userId, newWeight);
        eventWeightSums.merge(eventId, weightDelta, (oldSum, delta) -> oldSum.add(delta, MATH_CONTEXT));

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
            BigDecimal oldWeight,
            BigDecimal newWeight,
            UserActionAvro action
    ) {
        BigDecimal otherWeight = eventUserWeights
                .getOrDefault(otherEventId, Map.of())
                .getOrDefault(userId, BigDecimal.ZERO);
        if (otherWeight.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        BigDecimal oldMin = min(oldWeight, otherWeight);
        BigDecimal newMin = min(newWeight, otherWeight);
        BigDecimal minDelta = newMin.subtract(oldMin, MATH_CONTEXT);

        BigDecimal newMinSum = getMinWeightSum(eventId, otherEventId).add(minDelta, MATH_CONTEXT);

        if (minDelta.compareTo(BigDecimal.ZERO) != 0) {
            putMinWeightSum(eventId, otherEventId, newMinSum);
        }
        if (newMinSum.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        BigDecimal eventSum = eventWeightSums.getOrDefault(eventId, BigDecimal.ZERO);
        BigDecimal otherEventSum = eventWeightSums.getOrDefault(otherEventId, BigDecimal.ZERO);

        if (eventSum.compareTo(BigDecimal.ZERO) == 0 || otherEventSum.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        BigDecimal denominator = eventSum
                .multiply(otherEventSum, MATH_CONTEXT)
                .sqrt(MATH_CONTEXT);

        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        BigDecimal score = newMinSum.divide(denominator, MATH_CONTEXT);

        long first = Math.min(eventId, otherEventId);
        long second = Math.max(eventId, otherEventId);

        log.info("Пересчитано сходство: eventA={}, eventB={}, score={}", first, second, score);

        return EventSimilarityAvro.newBuilder()
                .setEventA(first)
                .setEventB(second)
                .setScore(score.doubleValue())
                .setTimestamp(action.getTimestamp())
                .build();
    }

    private BigDecimal getWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> VIEW_WEIGHT;
            case REGISTER -> REGISTER_WEIGHT;
            case LIKE -> LIKE_WEIGHT;
        };
    }

    private BigDecimal getMinWeightSum(long eventA, long eventB) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        return minWeightsSum
                .getOrDefault(first, Map.of())
                .getOrDefault(second, BigDecimal.ZERO);
    }

    private void putMinWeightSum(long eventA, long eventB, BigDecimal sum) {
        long first = Math.min(eventA, eventB);
        long second = Math.max(eventA, eventB);

        minWeightsSum
                .computeIfAbsent(first, id -> new HashMap<>())
                .put(second, sum);
    }

    private BigDecimal min(BigDecimal first, BigDecimal second) {
        if (first.compareTo(second) <= 0) {
            return first;
        }
        return second;
    }
}
