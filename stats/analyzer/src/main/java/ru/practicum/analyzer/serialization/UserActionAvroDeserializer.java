package ru.practicum.analyzer.serialization;

import ru.practicum.ewm.stats.avro.UserActionAvro;
import stats.avro.serialization.BaseAvroDeserializer;

public class UserActionAvroDeserializer extends BaseAvroDeserializer<UserActionAvro> {
    public UserActionAvroDeserializer() {
        super(UserActionAvro.getClassSchema());
    }
}
