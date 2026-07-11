package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.interactionapi.client.api.UserInternalApi;
import ru.practicum.interactionapi.dto.comment.CommentEventDto;
import ru.practicum.interactionapi.dto.user.UserShortDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentInternalServiceImpl implements CommentInternalService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserInternalApi userClient;

    @Override
    public List<CommentEventDto> findPublishedByEventIds(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return List.of();
        }

        List<Comment> comments = commentRepository.findPublishedByEventIds(eventIds);

        if (comments.isEmpty()) {
            return List.of();
        }

        List<Long> authorIds = comments.stream()
                .map(Comment::getAuthorId)
                .distinct()
                .toList();

        Map<Long, UserShortDto> authors = userClient.getUserShortDtos(authorIds).stream()
                .collect(Collectors.toMap(
                        UserShortDto::getId,
                        Function.identity(),
                        (first, second) -> first
                ));
        List<CommentEventDto> result = new ArrayList<>();

        for (Comment comment : comments) {
            UserShortDto author = authors.get(comment.getAuthorId());

            String authorName = null;
            if (author != null) {
                authorName = author.getName();
            }
            result.add(commentMapper.toCommentEventDto(comment, authorName));
        }
        return result;
    }
}
