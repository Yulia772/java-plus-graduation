package ru.practicum.collector.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("collector.kafka")
public class CollectorKafkaConfig {

    private String bootstrapServers;
    private Topic topic;

    @Getter
    @Setter
    public static class Topic {
        private String userActions;
    }
}
