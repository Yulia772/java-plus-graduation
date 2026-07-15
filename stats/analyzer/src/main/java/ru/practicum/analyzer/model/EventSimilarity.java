package ru.practicum.analyzer.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_similarities")
public class EventSimilarity {

    @EmbeddedId
    private EventSimilarityId id;

    @Column(name = "score", nullable = false)
    private Double score;

    @Column(name = "event_timestamp", nullable = false)
    private Instant eventTimestamp;
}
