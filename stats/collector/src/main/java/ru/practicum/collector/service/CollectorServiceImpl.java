package ru.practicum.collector.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.practicum.collector.config.CollectorKafkaConfig;
import ru.practicum.collector.mapper.UserActionMapper;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.message.UserActionProto;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectorServiceImpl implements CollectorService {

    private final CollectorKafkaConfig kafkaConfig;
    private final UserActionMapper mapper;
    private final KafkaTemplate<Long, Object> kafkaTemplate;

    @Override
    public void collectUserAction(UserActionProto userAction) {
        UserActionAvro userActionAvro = mapper.toAvro(userAction);
        String topic = kafkaConfig.getTopic().getUserActions();

        kafkaTemplate.send(
                topic,
                userAction.getUserId(),
                userActionAvro
        );

        log.info("Отправлено действие пользователя в Kafka: userId={}, eventId={}, actionType={}",
                userAction.getUserId(),
                userAction.getEventId(),
                userAction.getActionType());
    }
}
