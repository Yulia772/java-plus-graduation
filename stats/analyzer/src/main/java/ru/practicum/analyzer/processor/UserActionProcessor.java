package ru.practicum.analyzer.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.config.AnalyzerKafkaConfig;
import ru.practicum.analyzer.serialization.UserActionAvroDeserializer;
import ru.practicum.analyzer.service.StatsStorageService;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionProcessor implements Runnable {

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    private final StatsStorageService statsStorageService;
    private final AnalyzerKafkaConfig kafkaConfig;

    @Override
    public void run() {
        KafkaConsumer<Long, UserActionAvro> consumer = new KafkaConsumer<>(getConsumerProperties());
        String userActionsTopic = kafkaConfig.getTopic().getUserActions();

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(userActionsTopic));
            log.info("Analyzer подписался на топик действий пользователей: {}", userActionsTopic);

            while (true) {
                ConsumerRecords<Long, UserActionAvro> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                records.forEach(record -> statsStorageService.saveUserAction(record.value()));

                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
            log.info("Останавливаем consumer действий пользователей");
        } catch (Exception e) {
            log.error("Ошибка во время обработки действий пользователей", e);
        } finally {
            try {
                consumer.commitSync();
            } catch (Exception e) {
                log.warn("Не удалось зафиксировать смещение consumer действий пользователей", e);
            } finally {
                consumer.close();
                log.info("Consumer действий пользователей закрыт");
            }
        }
    }

    private Properties getConsumerProperties() {
        Properties properties = new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getConsumer().getGroupId());

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, UserActionAvroDeserializer.class.getName());

        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        return properties;
    }
}