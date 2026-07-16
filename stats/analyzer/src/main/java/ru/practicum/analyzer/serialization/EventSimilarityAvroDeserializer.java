package ru.practicum.analyzer.serialization;

import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import stats.avro.serialization.BaseAvroDeserializer;

public class EventSimilarityAvroDeserializer extends BaseAvroDeserializer<EventSimilarityAvro> {
    public EventSimilarityAvroDeserializer() {
        super(EventSimilarityAvro.getClassSchema());
    }
}
