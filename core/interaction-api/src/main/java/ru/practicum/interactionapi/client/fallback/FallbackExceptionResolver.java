package ru.practicum.interactionapi.client.fallback;

import ru.practicum.interactionapi.exception.BadRequestException;
import ru.practicum.interactionapi.exception.ConflictException;
import ru.practicum.interactionapi.exception.NotFoundException;
import ru.practicum.interactionapi.exception.ServiceUnavailableException;

final class FallbackExceptionResolver {

    private FallbackExceptionResolver() {
    }
    static RuntimeException resolve(Throwable cause, String serviceName) {
        Throwable current = cause;
        while (current != null) {
            if (current instanceof BadRequestException exception) {
                return exception;
            }
            if (current instanceof NotFoundException exception) {
                return exception;
            }
            if (current instanceof ConflictException exception) {
                return exception;
            }
            if (current instanceof ServiceUnavailableException exception) {
                return exception;
            }
            current = current.getCause();
        }
        return new ServiceUnavailableException(serviceName, cause);
    }
}
