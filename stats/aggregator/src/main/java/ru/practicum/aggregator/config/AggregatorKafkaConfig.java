package ru.practicum.aggregator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("aggregator.kafka")
public class AggregatorKafkaConfig {

    private String bootstrapServers;
    private Topic topic;

    @Getter
    @Setter
    public static class Topic {
        private String userActions;
        private String eventsSimilarity;
    }
}
