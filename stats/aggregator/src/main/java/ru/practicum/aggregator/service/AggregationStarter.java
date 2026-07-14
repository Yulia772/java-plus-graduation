package ru.practicum.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.config.AggregatorKafkaConfig;
import ru.practicum.aggregator.serialization.UserActionAvroDeserializer;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import stats.avro.serialization.AvroSerializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);
    private static final String CONSUMER_GROUP_ID = "aggregator";

    private final AggregatorService aggregatorService;
    private final AggregatorKafkaConfig kafkaConfig;

    public void start() {

        KafkaConsumer<Long, UserActionAvro> consumer = new KafkaConsumer<>(getConsumerProperties());
        KafkaProducer<Long, EventSimilarityAvro> producer = new KafkaProducer<>(getProducerProperties());

        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            String userActionsTopic = kafkaConfig.getTopic().getUserActions();
            String similaritiesTopic = kafkaConfig.getTopic().getEventsSimilarity();

            consumer.subscribe(List.of(userActionsTopic));
            log.info("Aggregator подписался на topic: {}", userActionsTopic);

            while (true) {
                ConsumerRecords<Long, UserActionAvro> records =
                        consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                for (ConsumerRecord<Long, UserActionAvro> record : records) {
                    UserActionAvro action = record.value();

                    log.info("Получено действие пользователя из Kafka: userId={}, eventId={}, actionType={}",
                            action.getUserId(),
                            action.getEventId(),
                            action.getActionType());

                    List<EventSimilarityAvro> similarities = aggregatorService.process(action);

                    for (EventSimilarityAvro similarity : similarities) {
                        producer.send(new ProducerRecord<>(
                                similaritiesTopic,
                                similarity.getEventA(),
                                similarity
                        ));
                        log.info("Отправлено сходство мероприятий в Kafka: eventA={}, eventB={}, score={}",
                                similarity.getEventA(),
                                similarity.getEventB(),
                                similarity.getScore());
                    }
                }

                consumer.commitSync();
            }

        } catch (WakeupException ignored) {
            // игнорируем - закрываем консьюмер и продюсер в блоке finally
        } catch (Exception e) {
            log.error("Ошибка во время обработки действий пользователей", e);
        } finally {

            try {
                log.info("Сбрасываем буфер продюсера");
                producer.flush();

                log.info("Фиксируем смещение консьюмера");
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();

                log.info("Закрываем продюсер");
                producer.close();
            }
        }
    }

    private Properties getConsumerProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP_ID);

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                LongDeserializer.class.getName());

        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                UserActionAvroDeserializer.class.getName());

        properties.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                OffsetResetStrategy.EARLIEST.name().toLowerCase());

        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return properties;
    }

    private Properties getProducerProperties() {
        Properties properties = new Properties();

        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());

        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());

        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                AvroSerializer.class.getName());
        return properties;
    }
}
