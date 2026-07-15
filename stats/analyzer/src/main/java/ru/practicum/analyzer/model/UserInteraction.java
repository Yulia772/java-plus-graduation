package ru.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_interactions")
public class UserInteraction {

    @EmbeddedId
    private UserInteractionId id;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private InteractionType actionType;

    @Column(name = "weight", nullable = false)
    private Double weight;

    @Column(name = "event_timestamp", nullable = false)
    private Instant eventTimestamp;
}
