package ru.practicum.analyzer.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.analyzer.model.InteractionType;
import ru.practicum.analyzer.model.UserInteraction;
import ru.practicum.analyzer.model.UserInteractionId;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Component
public class UserInteractionMapper {

    public UserInteractionId toId(UserActionAvro action) {
        return new UserInteractionId(
                action.getUserId(),
                action.getEventId()
        );
    }

    public UserInteraction toEntity(UserActionAvro action) {
        return UserInteraction.builder()
                .id(toId(action))
                .actionType(toInteractionType(action.getActionType()))
                .weight(getWeight(action.getActionType()))
                .eventTimestamp(action.getTimestamp())
                .build();
    }

    public void updateEntity(UserInteraction interaction, UserActionAvro action) {
        interaction.setActionType(toInteractionType(action.getActionType()));
        interaction.setWeight(getWeight(action.getActionType()));
        interaction.setEventTimestamp(action.getTimestamp());
    }

    public double getWeight(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
        };
    }

    private InteractionType toInteractionType(ActionTypeAvro actionType) {
        return switch (actionType) {
            case VIEW -> InteractionType.VIEW;
            case REGISTER -> InteractionType.REGISTER;
            case LIKE -> InteractionType.LIKE;
        };
    }
}
