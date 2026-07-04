package ru.practicum.mapper;


import ru.practicum.dto.request.EndPointHitDtoNew;
import ru.practicum.dto.response.EndPointHitDto;
import ru.practicum.dto.response.ViewStatsDto;
import ru.practicum.model.EndPointHit;

//задаёт контракт для всех мапперов в проекте
public interface StatsMapper {
    EndPointHit toEntity(EndPointHitDtoNew dto); //из DTO для создания в сущность БД;

    EndPointHitDto toDto(EndPointHit entity);//из сущности БД в полный DTO

    ViewStatsDto toViewStatsDto(String app, String uri, Long hits);// создание DTO статистики из отдельных параметров
}
