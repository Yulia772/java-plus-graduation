package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.analyzer.model.EventSimilarityId;

import java.util.Collection;
import java.util.List;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, EventSimilarityId> {

    @Query("""
            SELECT es
            FROM EventSimilarity es
            WHERE es.id.eventA IN :eventIds
            OR es.id.eventB IN :eventIds
            """)
    List<EventSimilarity> findAllByEventIds(Collection<Long> eventIds);

    @Query("""
            SELECT es
            FROM EventSimilarity es
            WHERE es.id.eventA = :eventId
            OR es.id.eventB = :eventId
            """)
    List<EventSimilarity> findAllByEventId(Long eventId);
}
