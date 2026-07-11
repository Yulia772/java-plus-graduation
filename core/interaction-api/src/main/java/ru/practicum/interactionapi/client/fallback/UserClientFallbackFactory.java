package ru.practicum.interactionapi.client.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.interactionapi.client.UserClient;
import ru.practicum.interactionapi.dto.user.UserShortDto;

import java.util.List;

@Slf4j
@Component
public class UserClientFallbackFactory
        implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable cause) {
        log.error("Ошибка при обращении к user-service", cause);

        RuntimeException exception = FallbackExceptionResolver.resolve(
                cause,
                "user-service"
        );

        return new UserClient() {

            @Override
            public void checkUserExists(Long userId) {
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
        };
    }
}

