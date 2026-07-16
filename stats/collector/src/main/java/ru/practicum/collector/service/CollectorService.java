package ru.practicum.collector.service;

import ru.practicum.ewm.stats.proto.message.UserActionProto;

public interface CollectorService {

    void collectUserAction(UserActionProto userAction);
}
