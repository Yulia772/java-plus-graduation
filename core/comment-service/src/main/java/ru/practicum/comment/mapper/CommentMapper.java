package ru.practicum.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.interactionapi.dto.comment.CommentFullDto;
import ru.practicum.interactionapi.dto.comment.CommentShortDto;
import ru.practicum.interactionapi.dto.comment.NewCommentDto;
import ru.practicum.comment.model.Comment;

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

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "event", ignore = true)
    CommentFullDto toCommentFullDto(Comment comment);
}
