package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.interactionapi.client.api.UserInternalApi;
import ru.practicum.interactionapi.dto.compilation.CompilationDto;
import ru.practicum.interactionapi.dto.compilation.NewCompilationDto;
import ru.practicum.interactionapi.dto.compilation.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.interactionapi.dto.event.EventShortDto;
import ru.practicum.interactionapi.dto.user.UserShortDto;
import ru.practicum.interactionapi.exception.NotFoundException;
import org.springframework.data.domain.Pageable;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;
    private final UserInternalApi userClient;

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto dto) {
        log.info("CompilationService: получен запрос на создание компиляции: {}.", dto);
        Set<Event> events = getEvents(dto.getEvents());
        Compilation entity = compilationMapper.toCompilation(dto, events);
        Compilation saved = compilationRepository.save(entity);
        CompilationDto result = buildCompilationDto(saved);
        log.info("CompilationService: компиляция сохранена: {}", result);
        return result;
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        log.info("CompilationService: получен запрос на удаление компиляции с id: {}.", compId);
        Compilation compilation = getCompilationOrThrow(compId);
        compilationRepository.delete(compilation);
        log.info("CompilationService: компиляция с id: {} удалена.", compId);
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest dto) {
        log.info("CompilationService: получен запрос на обновление компиляции с id {}.", compId);
        Compilation saved = getCompilationOrThrow(compId);
        updateFields(saved, dto);
        CompilationDto result = buildCompilationDto(saved);
        log.info("CompilationService: компиляция обновлена: {}", result);
        return result;
    }

    @Override
    public List<CompilationDto> get(Pageable pageable, Boolean pinned) {
        log.info("CompilationService: получен запрос на получение списка компиляций.");
        Page<Long> pageIds = compilationRepository.findCompilationIds(pinned, pageable);

        List<Compilation> saved = pageIds.isEmpty()
                ? List.of()
                : compilationRepository.findAllCompilationsWithEvents(pageIds.getContent());

        Set<Event> allEvents = collectAllEvents(saved);
        Map<Long, UserShortDto> initiatorsMap = fetchInitiators(allEvents);

        List<CompilationDto> result = new ArrayList<>();
        for (Compilation compilation : saved) {
            CompilationDto dto = buildCompilationDtoWithKnownInitiators(compilation, initiatorsMap);
            result.add(dto);
        }
        log.info("CompilationService: выдана страница компиляций размером: {}, начиная с {}.",
                saved.size(), pageable.getOffset());
        return result;
    }

    @Override
    public CompilationDto getById(Long compId) {
        log.info("CompilationService: получен запрос на получение компиляции с id: {}.", compId);
        Compilation saved = getCompilationOrThrow(compId);
        CompilationDto result = buildCompilationDto(saved);
        log.info("CompilationService: компиляция выдана: {}", result);
        return result;
    }

    private Set<Event> getEvents(Set<Long> eventIds) {
        Set<Event> result = new HashSet<>();

        if (eventIds != null && !eventIds.isEmpty()) {
            result.addAll(eventRepository.findAllWithCategory(eventIds));

            if (eventIds.size() > result.size()) {
                throw new NotFoundException("В полученном списке событий есть не существующие.");
            }
        }
        return result;
    }

    private void updateFields(Compilation compilation, UpdateCompilationRequest dto) {
        if (dto.getEvents() != null) {
            compilation.setEvents(getEvents(dto.getEvents()));
        }

        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }

        if (dto.getTitle() != null) {
            compilation.setTitle(dto.getTitle());
        }
    }

    private Compilation getCompilationOrThrow(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Компиляция с id=" + id + " не найдена"));
    }

    private Map<Long, UserShortDto> fetchInitiators(Collection<Event> events) {
        if (events == null || events.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> userIds = events.stream()
                .map(Event::getInitiatorId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        List<UserShortDto> users = userClient.getUserShortDtos(userIds);

        Map<Long, UserShortDto> result = new HashMap<>();
        for (UserShortDto user : users) {
            result.put(user.getId(), user);
        }
        return result;
    }

    private Set<Event> collectAllEvents(List<Compilation> compilations) {
        if (compilations == null || compilations.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Event> allEvents = new HashSet<>();
        for (Compilation compilation : compilations) {
            if (compilation.getEvents() != null) {
                allEvents.addAll(compilation.getEvents());
            }
        }
        return allEvents;
    }

    private CompilationDto buildCompilationDtoWithKnownInitiators(Compilation compilation,
                                                                  Map<Long, UserShortDto> initiatorsMap) {
        CompilationDto dto = compilationMapper.toCompilationDto(compilation);

        List<EventShortDto> eventDtos = new ArrayList<>();

        if (compilation.getEvents() != null) {
            for (Event event : compilation.getEvents()) {
                UserShortDto initiator = initiatorsMap.get(event.getInitiatorId());

                EventShortDto eventDto = eventMapper.toEventShortDto(
                        event, initiator, 0, 0
                );
                eventDtos.add(eventDto);
            }
        }
        eventDtos.sort(Comparator.comparing(EventShortDto::getId));
        dto.setEvents(eventDtos);

        return dto;
    }

    private CompilationDto buildCompilationDto(Compilation compilation) {
        Set<Event> events = compilation.getEvents() == null
                ? Collections.emptySet()
                : compilation.getEvents();
        Map<Long, UserShortDto> initiatorsMap = fetchInitiators(events);

        return buildCompilationDtoWithKnownInitiators(compilation, initiatorsMap);
    }
}
