package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.dto.EventScore;
import ru.practicum.analyzer.model.EventSimilarity;
import ru.practicum.analyzer.model.UserInteraction;
import ru.practicum.analyzer.repository.EventSimilarityRepository;
import ru.practicum.analyzer.repository.UserInteractionRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final UserInteractionRepository userInteractionRepository;
    private final EventSimilarityRepository eventSimilarityRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EventScore> getRecommendationsForUser(Long userId, int maxResults) {
        if (maxResults <= 0) {
            return List.of();
        }

        List<UserInteraction> userInteractions = userInteractionRepository.findByIdUserId(userId);
        if (userInteractions.isEmpty()) {
            return List.of();
        }

        Map<Long, Double> userWeights = new HashMap<>();
        Set<Long> userEventIds = new HashSet<>();

        for (UserInteraction interaction : userInteractions) {
            Long eventId = interaction.getId().getEventId();

            userEventIds.add(eventId);
            userWeights.put(eventId, interaction.getWeight());
        }

        List<EventSimilarity> similarities = eventSimilarityRepository.findAllByEventIds(userEventIds);

        Map<Long, Double> numeratorByEvent = new HashMap<>();
        Map<Long, Double> denominatorByEvent = new HashMap<>();

        for (EventSimilarity similarity : similarities) {
            Long eventA = similarity.getId().getEventA();
            Long eventB = similarity.getId().getEventB();
            Double similarityScore = similarity.getScore();

            Long candidateEventId = null;
            Double userWeight = null;

            if (userWeights.containsKey(eventA) && !userEventIds.contains(eventB)) {
                candidateEventId = eventB;
                userWeight = userWeights.get(eventA);
            }

            if (userWeights.containsKey(eventB) && !userEventIds.contains(eventA)) {
                candidateEventId = eventA;
                userWeight = userWeights.get(eventB);
            }

            if (candidateEventId == null || userWeight == null || similarityScore == 0.0) {
                continue;
            }

            numeratorByEvent.merge(
                    candidateEventId,
                    similarityScore * userWeight,
                    Double::sum
            );
            denominatorByEvent.merge(
                    candidateEventId,
                    similarityScore,
                    Double::sum
            );
        }

        List<EventScore> recommendations = new ArrayList<>();
        for (Long eventId : numeratorByEvent.keySet()) {
            Double numerator = numeratorByEvent.get(eventId);
            Double denominator = denominatorByEvent.get(eventId);

            if (denominator == null || denominator == 0.0) {
                continue;
            }

            recommendations.add(new EventScore(eventId, numerator / denominator));
        }
        return sortAndLimit(recommendations, maxResults);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventScore> getSimilarEvents(Long eventId, Long userId, int maxResults) {
        if (maxResults <= 0) {
            return List.of();
        }

        Set<Long> userEventIds = getUserEventIds(userId);
        List<EventSimilarity> similarities = eventSimilarityRepository.findAllByEventId(eventId);

        List<EventScore> result = new ArrayList<>();

        for (EventSimilarity similarity : similarities) {
            Long eventA = similarity.getId().getEventA();
            Long eventB = similarity.getId().getEventB();

            Long similarEventId;

            if (eventA.equals(eventId)) {
                similarEventId = eventB;
            } else {
                similarEventId = eventA;
            }

            if (userEventIds.contains(similarEventId)) {
                continue;
            }

            result.add(new EventScore(similarEventId, similarity.getScore()));
        }

        return sortAndLimit(result, maxResults);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventScore> getInteractionsCount(List<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return List.of();
        }

        List<Long> uniqueEventIds = new ArrayList<>(new LinkedHashSet<>(eventIds));
        List<UserInteraction> interactions = userInteractionRepository.findAllByEventIds(uniqueEventIds);

        Map<Long, Double> scoreByEventId = new HashMap<>();

        for (UserInteraction interaction : interactions) {
            Long eventId = interaction.getId().getEventId();

            scoreByEventId.merge(
                    eventId,
                    interaction.getWeight(),
                    Double::sum
            );
        }

        List<EventScore> result = new ArrayList<>();

        for (Long eventId : uniqueEventIds) {
            Double score = scoreByEventId.get(eventId);

            if (score != null) {
                result.add(new EventScore(eventId, score));
            }
        }
        return result;
    }

    private Set<Long> getUserEventIds(Long userId) {
        List<UserInteraction> userInteractions = userInteractionRepository.findByIdUserId(userId);
        Set<Long> eventIds = new HashSet<>();

        for (UserInteraction interaction : userInteractions) {
            eventIds.add(interaction.getId().getEventId());
        }
        return eventIds;
    }

    private List<EventScore> sortAndLimit(List<EventScore> scores, int maxResults) {
        scores.sort(Comparator
                .comparing(EventScore::getScore)
                .reversed()
                .thenComparing(EventScore::getEventId));
        int limit = Math.min(scores.size(), maxResults);
        return new ArrayList<>(scores.subList(0, limit));
    }
}
