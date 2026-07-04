package ru.practicum.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "endpoint_hits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndPointHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "app", nullable = false, length = 50)
    private String app;

    @Column(name = "uri", nullable = false, length = 2000)
    private String uri;

    @Column(name = "ip", nullable = false, length = 45)
    private String ip;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
