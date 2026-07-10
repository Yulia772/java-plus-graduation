package ru.practicum.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.interactionapi.dto.comment.CommentEventDto;
import ru.practicum.interactionapi.dto.comment.CommentFullDto;
import ru.practicum.interactionapi.dto.comment.CommentShortDto;
import ru.practicum.interactionapi.dto.comment.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.interactionapi.dto.user.UserShortDto;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "text", source = "text")
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "eventId", ignore = true)
    @Mapping(target = "state", ignore = true)
    Comment toComment(NewCommentDto dto);

    CommentShortDto toCommentShortDto(Comment comment);

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "event", ignore = true)
    CommentFullDto toCommentFullDto(Comment comment, UserShortDto author);

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "eventId", source = "comment.eventId")
    @Mapping(target = "authorName", source = "authorName")
    CommentEventDto toCommentEventDto(Comment comment, String authorName);
}
