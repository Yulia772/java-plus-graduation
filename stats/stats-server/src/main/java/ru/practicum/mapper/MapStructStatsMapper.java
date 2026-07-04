package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.dto.request.EndPointHitDtoNew;
import ru.practicum.dto.response.EndPointHitDto;
import ru.practicum.dto.response.ViewStatsDto;
import ru.practicum.model.EndPointHit;

/**
 * Маппер для преобразования между DTO и сущностями статистики.
 * Использует MapStruct для автоматической генерации кода преобразования.
 */
@Mapper(componentModel = "spring")
//указывает MapStruct сгенерировать реализацию как Spring‑бин, доступный через @Autowired;
public interface MapStructStatsMapper extends StatsMapper {

    /**
     * Преобразует DTO для создания нового хита в сущность БД.
     * Игнорирует поле ID, так как оно будет сгенерировано базой данных.
     */
    @Override
    @Mapping(target = "id", ignore = true)
    EndPointHit toEntity(EndPointHitDtoNew dto);

    /**
     * Преобразует сущность БД в полный DTO хита.
     */
    @Override
    EndPointHitDto toDto(EndPointHit entity);

    /**
     * Создаёт DTO статистики из отдельных параметров.
     * Используется для агрегатных запросов в репозитории.
     */
    default ViewStatsDto toViewStatsDto(String app, String uri, Long hits) {
        return ViewStatsDto.builder()
                .app(app)
                .uri(uri)
                .hits(hits)
                .build();
    }
}
