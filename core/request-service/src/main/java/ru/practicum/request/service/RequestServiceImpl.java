package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interactionapi.client.EventClient;
import ru.practicum.interactionapi.client.UserClient;
import ru.practicum.interactionapi.dto.event.State;
import ru.practicum.interactionapi.dto.request.RequestEventInfo;
import ru.practicum.interactionapi.exception.ConditionsNotMetException;
import ru.practicum.interactionapi.exception.NotFoundException;
import ru.practicum.interactionapi.dto.request.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.interactionapi.dto.request.RequestStatus;
import ru.practicum.request.repository.RequestRepository;

import java.util.List;

/**
 * Сервис для работы с запросами на участие в событиях
 * Реализует бизнес-логику создания, отмены и получения запросов
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository reqRepository;
    private final UserClient userClient;
    private final EventClient eventClient;
    private final RequestMapper requestMapper;

    /**
     * Создание нового запроса на участие в событии
     *
     * @param userId  идентификатор пользователя, создающего запрос
     * @param eventId идентификатор события, на которое создается запрос
     * @return DTO созданного запроса
     * @throws NotFoundException         если пользователь или событие не найдены
     * @throws ConditionsNotMetException если нарушены условия создания запроса:
     *                                   - запрос уже существует
     *                                   - пользователь является инициатором события
     *                                   - событие не опубликовано
     *                                   - достигнут лимит участников
     */
    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, Long eventId) {
        log.info("Создание запроса: userId={}, eventId={}", userId, eventId);

        userClient.checkUserExists(userId);
        RequestEventInfo event = eventClient.getEventInfo(eventId);

        //Проверка на повторный запрос (нельзя подать заявку дважды)
        if (reqRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            log.warn("Запрос уже существует: userId={}, eventId={}", userId, eventId);
            throw new ConditionsNotMetException("Запрос уже существует");
        }

        // Проверка, что инициатор не подает заявку на свое событие
        if (event.initiatorId().equals(userId)) {
            log.warn("Инициатор не может создать запрос на свое событие: userId={}, eventId={}", userId, eventId);
            throw new ConditionsNotMetException("Инициатор не может создать запрос на свое событие");
        }

        // Проверка, что событие опубликовано (нельзя подать заявку на черновик)
        if (!event.state().equals(State.PUBLISHED)) {
            log.warn("Событие не опубликовано: eventId={}, state={}", eventId, event.state());
            throw new ConditionsNotMetException("Событие должно быть опубликовано");
        }

        // Проверка лимита участников
        long confirmedCount = getConfirmedCount(eventId);

        if (event.partLimit() > 0 && event.partLimit() <= confirmedCount) {
            log.debug("Достигнут лимит участников: eventId={}, limit={}, confirmed={}",
                    eventId, event.partLimit(), confirmedCount);
            throw new ConditionsNotMetException("Достигнут лимит участников");
        }

        Request request = requestMapper.toRequest(userId, eventId);

        //  Автоматическое подтверждение только если отключена модерация
        //    Если лимит = 0 - означает отсутствие лимита, но не означает автоматическое подтверждение заявки
        //    Если requestModeration = false - модерация не требуется, подтверждаем сразу
        if (Boolean.FALSE.equals(event.requestModeration())) {
            log.debug("Модерация не требуется, статус CONFIRMED: request для eventId={}", eventId);
            request.setStatus(RequestStatus.CONFIRMED);
        }

        Request saved = reqRepository.save(request);
        log.info("Запрос создан: id={}, userId={}, eventId={}", saved.getId(), userId, eventId);

        return requestMapper.toParticipantRequestDto(saved);
    }

    /**
     * Получение всех запросов пользователя
     */
    @Override
    public List<ParticipationRequestDto> findAll(Long userId) {
        log.info("Получение всех запросов пользователя: userId={}", userId);

        userClient.checkUserExists(userId);

        List<Request> requests = reqRepository.findAllByRequesterId(userId);

        log.info("Найдено запросов: {} для userId={}", requests.size(), userId);

        return requestMapper.toParticipantRequestDto(requests);
    }

    /**
     * Отмена запроса на участие
     *
     * @param userId    идентификатор пользователя, отменяющего запрос
     * @param requestId идентификатор отменяемого запроса
     * @return DTO отмененного запроса
     */
    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Отмена запроса: userId={}, requestId={}", userId, requestId);

        userClient.checkUserExists(userId);
        Request request = findRequestById(requestId);

        //Проверяем, что пользователь действительно создатель запроса
        //    (нельзя отменить чужой запрос)
        if (!request.getRequesterId().equals(userId)) {
            log.warn("Пользователь не является создателем запроса: userId={}, requestId={}", userId, requestId);
            throw new ConditionsNotMetException(
                    String.format("Пользователь %d не является создателем запроса %d", userId, requestId)
            );
        }

        request.setStatus(RequestStatus.CANCELED);

        Request saved = reqRepository.save(request);

        log.info("Запрос отменен: userId={}, requestId={}", userId, requestId);

        return requestMapper.toParticipantRequestDto(saved);
    }

    /**
     * Поиск запроса по ID с проверкой существования
     */
    private Request findRequestById(Long id) {
        return reqRepository.findById(id).orElseThrow(() -> {
            log.warn("Запрос не найден: id={}", id);
            return new NotFoundException("Запрос с id=" + id + " не найден");
        });
    }

    /**
     * Получение количества подтвержденных запросов на событие
     */
    private long getConfirmedCount(Long eventId) {
        return reqRepository.confirmedCount(eventId);
    }

}