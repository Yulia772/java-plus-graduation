package ru.practicum.interactionapi.exception;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String serviceName, Throwable cause) {

        super("Сервис " + serviceName + " временно недоступен.", cause);
    }
}
