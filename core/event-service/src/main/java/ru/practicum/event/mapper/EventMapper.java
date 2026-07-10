package ru.practicum.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.interactionapi.dto.comment.CommentEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.interactionapi.dto.event.EventFullDto;
import ru.practicum.interactionapi.dto.event.EventShortDto;
import ru.practicum.interactionapi.dto.event.NewEventDto;
import ru.practicum.interactionapi.dto.user.UserShortDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface EventMapper {

    //Создание Event из NewEventDto
    //category, initiator, createdOn, publishedOn, state — заполняем в сервисе

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "initiatorId", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "locLat", source = "location.lat")
    @Mapping(target = "locLon", source = "location.lon")
    @Mapping(target = "partLimit", source = "participantLimit")
    Event toEvent(NewEventDto dto);


    @Mapping(target = "annotation", source = "event.annotation")
    @Mapping(target = "category", source = "event.category")
    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    @Mapping(target = "createdOn", source = "event.createdOn")
    @Mapping(target = "description", source = "event.description")
    @Mapping(target = "eventDate", source = "event.eventDate")
    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "location",
            expression = "java(new Location(event.getLocLat(), event.getLocLon()))")
    @Mapping(target = "paid", source = "event.paid")
    @Mapping(target = "participantLimit", source = "event.partLimit")
    @Mapping(target = "publishedOn", source = "event.publishedOn")
    @Mapping(target = "requestModeration", source = "event.requestModeration")
    @Mapping(target = "state", source = "event.state")
    @Mapping(target = "title", source = "event.title")
    @Mapping(target = "views", source = "views")
    @Mapping(target = "comments", source = "comments")
    EventFullDto toEventFullDto(Event event,
                                UserShortDto initiator,
                                int confirmedRequests,
                                int views,
                                List<CommentEventDto> comments);

    //Короткий DTO события
    @Mapping(target = "annotation", source = "event.annotation")
    @Mapping(target = "category", source = "event.category")
    @Mapping(target = "confirmedRequests", source = "confirmedRequests")
    @Mapping(target = "eventDate", source = "event.eventDate")
    @Mapping(target = "id", source = "event.id")
    @Mapping(target = "initiator", source = "initiator")
    @Mapping(target = "paid", source = "event.paid")
    @Mapping(target = "title", source = "event.title")
    @Mapping(target = "views", source = "views")
    EventShortDto toEventShortDto(Event event,
                                  UserShortDto initiator,
                                  int confirmedRequests,
                                  int views);
}