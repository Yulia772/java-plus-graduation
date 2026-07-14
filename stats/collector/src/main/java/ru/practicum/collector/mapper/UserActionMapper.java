package ru.practicum.collector.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.proto.message.ActionTypeProto;
import ru.practicum.ewm.stats.proto.message.UserActionProto;

import java.time.Instant;

@Component
public class UserActionMapper {

    public UserActionAvro toAvro(UserActionProto proto) {
        return UserActionAvro.newBuilder()
                .setUserId(proto.getUserId())
                .setEventId(proto.getEventId())
                .setActionType(toAvro(proto.getActionType()))
                .setTimestamp(toInstant(proto))
                .build();
    }

    private ActionTypeAvro toAvro(ActionTypeProto actionType) {
        return switch (actionType) {
            case ACTION_VIEW -> ActionTypeAvro.VIEW;
            case ACTION_REGISTER -> ActionTypeAvro.REGISTER;
            case ACTION_LIKE -> ActionTypeAvro.LIKE;
            case UNRECOGNIZED -> throw new IllegalArgumentException("Неизвестный actionType: " + actionType);
        };
    }

    private Instant toInstant(UserActionProto proto) {
        return Instant.ofEpochSecond(
                proto.getTimestamp().getSeconds(),
                proto.getTimestamp().getNanos()
        );
    }
}
