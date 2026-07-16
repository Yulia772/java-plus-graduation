package ru.practicum.analyzer.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.analyzer.model.EventSimilarityId;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@Component
public class EventSimilarityMapper {

    public EventSimilarityId toId(EventSimilarityAvro similarity) {
        long eventA = Math.min(similarity.getEventA(), similarity.getEventB());
        long eventB = Math.max(similarity.getEventA(), similarity.getEventB());

        return new EventSimilarityId(eventA, eventB);
    }

    public EventSimilarity toEntity(EventSimilarityAvro similarity) {
        return EventSimilarity.builder()
                .id(toId(similarity))
                .score(similarity.getScore())
                .eventTimestamp(similarity.getTimestamp())
                .build();
    }

    public void updateEntity(EventSimilarity eventSimilarity, EventSimilarityAvro similarity) {
        eventSimilarity.setScore(similarity.getScore());
        eventSimilarity.setEventTimestamp(similarity.getTimestamp());
    }

    public boolean isSameEventPair(EventSimilarityAvro similarity) {
        return similarity.getEventA() == similarity.getEventB();
    }
}
