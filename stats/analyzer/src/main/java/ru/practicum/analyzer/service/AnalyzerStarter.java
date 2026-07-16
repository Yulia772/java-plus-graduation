package ru.practicum.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.processor.EventSimilarityProcessor;
import ru.practicum.analyzer.processor.UserActionProcessor;

@Component
@RequiredArgsConstructor
public class AnalyzerStarter {
    private final UserActionProcessor userActionProcessor;
    private final EventSimilarityProcessor eventSimilarityProcessor;

    public void start() {
        new Thread(userActionProcessor, "user-action-processor").start();
        new Thread(eventSimilarityProcessor, "event-similarity-processor").start();
    }
}
