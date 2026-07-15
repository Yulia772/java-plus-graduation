package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.mapper.EventSimilarityMapper;
import ru.practicum.analyzer.mapper.UserInteractionMapper;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.analyzer.model.EventSimilarityId;
import ru.practicum.analyzer.model.UserInteraction;
import ru.practicum.analyzer.model.UserInteractionId;
import ru.practicum.analyzer.repository.EventSimilarityRepository;
import ru.practicum.analyzer.repository.UserInteractionRepository;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatsStorageServiceImpl implements StatsStorageService {

    private final UserInteractionRepository userInteractionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;
    private final UserInteractionMapper userInteractionMapper;
    private final EventSimilarityMapper eventSimilarityMapper;

    @Override
    @Transactional
    public void saveUserAction(UserActionAvro action) {
        UserInteractionId id = userInteractionMapper.toId(action);
        Optional<UserInteraction> optionalInteraction = userInteractionRepository.findById(id);

        if (optionalInteraction.isEmpty()) {
            UserInteraction interaction = userInteractionMapper.toEntity(action);
            userInteractionRepository.save(interaction);
            return;
        }

        UserInteraction existingInteraction = optionalInteraction.get();
        double newWeight = userInteractionMapper.getWeight(action.getActionType());

        if (newWeight > existingInteraction.getWeight()) {
            userInteractionMapper.updateEntity(existingInteraction, action);
            userInteractionRepository.save(existingInteraction);
        }
    }

    @Override
    @Transactional
    public void saveEventSimilarity(EventSimilarityAvro similarity) {
        if (eventSimilarityMapper.isSameEventPair(similarity)) {
            return;
        }

        EventSimilarityId id = eventSimilarityMapper.toId(similarity);
        Optional<EventSimilarity> optionalSimilarity = eventSimilarityRepository.findById(id);

        if (optionalSimilarity.isEmpty()) {
            EventSimilarity eventSimilarity = eventSimilarityMapper.toEntity(similarity);
            eventSimilarityRepository.save(eventSimilarity);
            return;
        }

        EventSimilarity existingSimilarity = optionalSimilarity.get();

        eventSimilarityMapper.updateEntity(existingSimilarity, similarity);
        eventSimilarityRepository.save(existingSimilarity);
    }
}

