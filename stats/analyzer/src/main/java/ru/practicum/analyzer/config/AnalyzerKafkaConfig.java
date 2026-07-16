package ru.practicum.analyzer.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("analyzer.kafka")
public class AnalyzerKafkaConfig {

    private String bootstrapServers;
    private Consumer consumer;
    private Topic topic;

    @Getter
    @Setter
    public static class Consumer {
        private String groupId;
    }

    @Getter
    @Setter
    public static class Topic {
        private String userActions;
        private String eventsSimilarity;
    }
}
