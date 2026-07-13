package ru.practicum.interactionapi.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.interactionapi.dto.event.EventShortDto;
import ru.practicum.interactionapi.dto.event.State;
import ru.practicum.interactionapi.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentFullDto {
    private Long id;
    private LocalDateTime createdOn;
    private String text;
    private UserShortDto author;
    private EventShortDto event;
    private State state;
}
