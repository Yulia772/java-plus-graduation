package ru.practicum.event.repository;

import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import ru.practicum.event.model.Event;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    boolean existsByCategoryId(Long categoryId);

    @Override
    @EntityGraph(attributePaths = "category")
    Page<Event> findAll(Predicate predicate, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "category")
    Optional<Event> findById(Long id);

    @Query("SELECT e FROM Event e " +
            "LEFT JOIN FETCH e.category " +
            "WHERE e.id in :ids")
    List<Event> findAllWithCategory(@Param("ids") Set<Long> ids);
}
