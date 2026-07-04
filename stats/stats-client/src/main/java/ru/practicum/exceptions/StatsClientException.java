package ru.practicum.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class StatsClientException extends RuntimeException {

    private final HttpStatusCode status;

    public StatsClientException(HttpStatusCode status, String body) {
        super("Ошибка вызова Stats-client, status: " + status + ", body: " + body);
        this.status = status;
    }
}
