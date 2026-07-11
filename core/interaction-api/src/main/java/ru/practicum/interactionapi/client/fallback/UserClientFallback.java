package ru.practicum.interactionapi.client.fallback;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.practicum.interactionapi.client.UserClient;
import ru.practicum.interactionapi.dto.user.UserShortDto;

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class UserClientFallback implements UserClient {

    private final RuntimeException exception;

    @Override
    public void checkUserExists(Long UserId) {
        throw exception;
    }

    @Override
    public UserShortDto getUserShortDto(Long userId) {
        throw exception;
    }

    @Override
    public List<UserShortDto> getUserShortDtos(List<Long> ids) {
        throw exception;
    }
}
