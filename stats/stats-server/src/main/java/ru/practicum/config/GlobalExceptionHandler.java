package ru.practicum.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик исключений для всего приложения.
 * Унифицирует ответы об ошибках в формате JSON.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает ошибки валидации DTO.
     * Возвращает 400 Bad Request с деталями полей, не прошедших валидацию.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.toList());
        logValidationError(ex, errors);
        return ErrorResponse.builder()
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .message("Ошибки валидации данных")
                .details(errors)
                .build();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleWrongPath(NoResourceFoundException ex) {
        String logMessage = String.format("Получен запрос на несуществующий путь %s.", ex.getResourcePath());
        log.warn(logMessage);

        return ErrorResponse.builder()
                .errorCode(HttpStatus.NOT_FOUND.value())
                .message("Ресурс по указанному пути не найден.")
                .build();
    }

    //Обрабатывает парсинг дат и проверки start/end
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(final IllegalArgumentException ex) {
        String errorMessage = "Ошибка в параметрах времени запроса.";

        log.warn("Ошибка в параметрах времени: {} - {}",
                ex.getClass().getSimpleName(), ex.getMessage());

        return ErrorResponse.builder()
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .message(errorMessage)
                .error(ex.getMessage())
                .build();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingParameter(MissingServletRequestParameterException ex) {
        String errorMessage = String.format("В запросе отсутствует параметр %s: %s",
                ex.getParameterName(), ex.getMessage());

        log.warn(errorMessage);

        return ErrorResponse.builder()
                .errorCode(HttpStatus.BAD_REQUEST.value())
                .message(errorMessage)
                .error(ex.getMessage())
                .build();
    }

    /**
     * Обрабатывает все остальные исключения.
     * Возвращает 500 Internal Server Error с деталями стека вызовов.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception ex) {
        String errorMessage = "Произошла ошибка на сервере.";
        log.error("Необработанное исключение: {} - {}", ex.getClass().getSimpleName(), ex.getMessage(), ex);
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String stackTrace = sw.toString();
        return ErrorResponse.builder()
                .errorCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("Произошла ошибка на сервере.")
                .error(ex.getMessage())
                .stackTrace(stackTrace)
                .build();
    }

    private String formatFieldError(FieldError error) {
        return String.format("Поле '%s': %s", error.getField(), error.getDefaultMessage());
    }

    private void logValidationError(MethodArgumentNotValidException ex, List<String> errors) {
        log.warn("Валидация не пройдена: {} ошибок в {} полях. Детали: {}",
                errors.size(),
                ex.getBindingResult().getErrorCount(),
                errors);
    }
}
