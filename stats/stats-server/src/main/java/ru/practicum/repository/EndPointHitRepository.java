package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.dto.response.ViewStatsDto;
import ru.practicum.model.EndPointHit;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с записями о хитах конечных точек.
 * Предоставляет методы для сохранения и агрегации статистики.
 */
public interface EndPointHitRepository extends JpaRepository<EndPointHit, Long> {

    /**
     * Получает статистику хитов по приложениям и URI за период.
     * Группирует по app и uri, сортирует по количеству хитов (по убыванию).
     */
    @Query("SELECT new ru.practicum.dto.response.ViewStatsDto(e.app, e.uri, COUNT(e.id)) " +
            "FROM EndPointHit e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.id) DESC")
    List<ViewStatsDto> getStats(@Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);


    /**
     * Получает статистику уникальных хитов (по IP‑адресам) за период.
     * Учитывает только уникальные IP для каждого app/uri.
     */
    @Query("SELECT new ru.practicum.dto.response.ViewStatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndPointHit e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<ViewStatsDto> getUniqueStats(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    /**
     * Получает статистику хитов с возможностью фильтрации по спискам URI
     */
    @Query("SELECT new ru.practicum.dto.response.ViewStatsDto(e.app, e.uri, COUNT(e.id)) " +
            "FROM EndPointHit e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR e.uri IN :uris) " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(e.id) DESC")
    List<ViewStatsDto> getStatsWithFilter(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end,
                                          @Param("uris") List<String> uris);

    /**
     * Получает статистику уникальных хитов с фильтрацией по спискам URI
     */
    @Query("SELECT new ru.practicum.dto.response.ViewStatsDto(e.app, e.uri, COUNT(DISTINCT e.ip)) " +
            "FROM EndPointHit e " +
            "WHERE e.timestamp BETWEEN :start AND :end " +
            "AND (:uris IS NULL OR e.uri IN :uris) " +
            "GROUP BY e.app, e.uri " +
            "ORDER BY COUNT(DISTINCT e.ip) DESC")
    List<ViewStatsDto> getUniqueStatsWithFilter(@Param("start") LocalDateTime start,
                                                @Param("end") LocalDateTime end,
                                                @Param("uris") List<String> uris);

}

