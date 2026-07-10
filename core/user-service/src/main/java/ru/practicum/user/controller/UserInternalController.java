package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.interactionapi.client.UserClient;
import ru.practicum.interactionapi.dto.user.UserShortDto;
import ru.practicum.user.service.UserInternalService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserInternalController implements UserClient {

    private final UserInternalService userInternalService;

    @Override
    public void checkUserExists(Long userId) {
        userInternalService.checkUserExists(userId);
    }

    @Override
    public UserShortDto getUserShortDto(Long userId) {
        return userInternalService.getUserShortDto(userId);
    }

    @Override
    public List<UserShortDto> getUserShortDtos(List<Long> ids) {
        return userInternalService.getUserShortDtos(ids);
    }
}
