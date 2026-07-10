package ru.practicum.interactionapi.client.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import ru.practicum.interactionapi.exception.BadRequestException;
import ru.practicum.interactionapi.exception.ConflictException;
import ru.practicum.interactionapi.exception.NotFoundException;

public class CommonFeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = "Ошибка при вызове сервиса: " + methodKey + ", status=" + response.status();

        return switch (response.status()) {
            case 400 -> new BadRequestException(message);
            case 404 -> new NotFoundException(message);
            case 409 -> new ConflictException(message);
            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}
