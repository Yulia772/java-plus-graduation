package ru.practicum.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.comment.model.Comment;

import java.util.List;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {

    @Query("SELECT DISTINCT c " +
            "FROM Comment c " +
            "WHERE c.id IN :commentIds " +
            "ORDER BY c.id")
    List<Comment> findAllByIdIn(@Param("commentIds") Set<Long> commentIds);

    @Query("SELECT c " +
            "FROM Comment c " +
            "WHERE c.eventId IN :eventIds " +
            "AND c.state = ru.practicum.interactionapi.dto.event.State.PUBLISHED")
    List<Comment> findPublishedByEventIds(@Param("eventIds") List<Long> eventIds);
}