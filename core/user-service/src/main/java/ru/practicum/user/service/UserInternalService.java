package ru.practicum.user.service;

import ru.practicum.interactionapi.dto.user.UserShortDto;

import java.util.List;

public interface UserInternalService {
    void checkUserExists(Long userId);

    UserShortDto getUserShortDto(Long userId);

    List<UserShortDto> getUserShortDtos(List<Long> ids);
}
