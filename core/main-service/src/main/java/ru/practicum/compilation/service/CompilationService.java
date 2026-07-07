package ru.practicum.compilation.service;

import ru.practicum.interactionapi.dto.compilation.CompilationDto;
import ru.practicum.interactionapi.dto.compilation.NewCompilationDto;
import ru.practicum.interactionapi.dto.compilation.UpdateCompilationRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CompilationService {
    CompilationDto create(NewCompilationDto dto);

    void delete(Long compId);

    CompilationDto update(Long compId, UpdateCompilationRequest dto);

    List<CompilationDto> get(Pageable pageable, Boolean pinned);

    CompilationDto getById(Long compId);
}
