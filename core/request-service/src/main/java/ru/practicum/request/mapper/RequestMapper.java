package ru.practicum.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.interactionapi.dto.request.ParticipationRequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.interactionapi.dto.request.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class, RequestStatus.class})
public interface RequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", expression = "java(LocalDateTime.now())")
    @Mapping(target = "eventId", source = "eventId")
    @Mapping(target = "requesterId", source = "userId")
    @Mapping(target = "status", expression = "java(RequestStatus.PENDING)")
    Request toRequest(Long userId, Long eventId);

    @Mapping(target = "event", source = "eventId")
    @Mapping(target = "requester", source = "requesterId")
    ParticipationRequestDto toParticipantRequestDto(Request request);

    List<ParticipationRequestDto> toParticipantRequestDto(List<Request> requests);
}