package ru.practicum.request.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.interactionapi.dto.request.RequestStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "requests", schema = "public")
@Getter
@Setter
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;

    @JoinColumn(name = "event_id", nullable = false)
    private Long eventId;

    @JoinColumn(name = "requester_id", nullable = false)
    private Long requesterId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}