package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.model.UserInteraction;
import ru.practicum.analyzer.model.UserInteractionId;

import java.util.List;

public interface UserInteractionRepository extends JpaRepository<UserInteraction, UserInteractionId> {
    List<UserInteraction> findByUserId(Long userId);

    List<UserInteraction> findByIdEventIdIn(Long eventId);
}
