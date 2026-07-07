package ru.practicum.interactionapi.exception;

public class ConditionsNotMetException extends RuntimeException {
    public ConditionsNotMetException(String msg) {
        super(msg);
    }
}
