package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.analyzer.model.UserInteraction;
import ru.practicum.analyzer.model.UserInteractionId;

import java.util.Collection;
import java.util.List;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, UserInteractionId> {
    List<UserInteraction> findByIdUserId(Long userId);

    @Query("""
            SELECT ui
            FROM UserInteraction ui
            WHERE ui.id.eventId IN :eventIds
            """)
    List<UserInteraction> findAllByEventIds(Collection<Long> eventIds);
}
