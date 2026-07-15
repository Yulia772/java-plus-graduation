package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.config.AnalyzerKafkaConfig;
import ru.practicum.analyzer.serialization.EventSimilarityAvroDeserializer;
import ru.practicum.analyzer.serialization.UserActionAvroDeserializer;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzerStarter {

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    private final StatsStorageService statsStorageService;
    private final AnalyzerKafkaConfig kafkaConfig;

    private final UserActionAvroDeserializer userActionDeserializer = new UserActionAvroDeserializer();
    private final EventSimilarityAvroDeserializer eventSimilarityDeserializer = new EventSimilarityAvroDeserializer();

    public void start() {

        KafkaConsumer<Long, byte[]> consumer = new KafkaConsumer<>(getConsumerProperties());

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(
                    kafkaConfig.getTopic().getUserActions(),
                    kafkaConfig.getTopic().getEventsSimilarity()
            ));

            while (true) {
                ConsumerRecords<Long, byte[]> records =
                        consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                for (ConsumerRecord<Long, byte[]> record : records) {
                    handleRecord(record);
                }
                consumer.commitSync();
            }

        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий Analyzer", e);
        } finally {

            try {
                log.info("Фиксируем смещение консьюмера");
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }

    private void handleRecord(ConsumerRecord<Long, byte[]> record) {
        String topic = record.topic();

        if (topic.equals(kafkaConfig.getTopic().getUserActions())) {
            UserActionAvro action = userActionDeserializer.deserialize(topic, record.value());
            statsStorageService.saveUserAction(action);
            return;
        }

        if (topic.equals(kafkaConfig.getTopic().getEventsSimilarity())) {
            EventSimilarityAvro similarity = eventSimilarityDeserializer.deserialize(topic, record.value());
            statsStorageService.saveEventSimilarity(similarity);
            return;
        }
        log.warn("Неизвестный topic: {}", topic);
    }

    private Properties getConsumerProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getConsumer().getGroupId());

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                LongDeserializer.class.getName());

        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                ByteArrayDeserializer.class.getName());

        properties.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return properties;
    }
}

