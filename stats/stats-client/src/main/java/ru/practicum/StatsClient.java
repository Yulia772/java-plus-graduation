package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;
import ru.practicum.dto.request.ViewStatsParamDto;
import ru.practicum.exceptions.StatsClientException;

import ru.practicum.dto.request.EndPointHitDtoNew;
import ru.practicum.dto.response.ViewStatsDto;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class StatsClient {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestClient restClient;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String statsServerUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(statsServerUrl)
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        (request, response) -> {
                            String body = new String(
                                    response.getBody().readAllBytes(),
                                    StandardCharsets.UTF_8
                            );

                            log.warn(
                                    "Ошибка вызова Stats-client, status: {}, body: {}",
                                    response.getStatusCode(),
                                    body
                            );

                            throw new StatsClientException(response.getStatusCode(), body);
                        }
                )
                .build();
    }

    public ResponseEntity<Void> hit(EndPointHitDtoNew hitDto) {
        log.info("Stats-client получен запрос на отправку данных - hitDto: {}", hitDto);
        ResponseEntity<Void> response = restClient.post()
                .uri(uriBuilder -> uriBuilder.path("/hit").build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitDto)
                .retrieve()
                .toBodilessEntity();
        log.info("Stats-server ответил на запрос Stats-client, статус ответа: {}", response.getStatusCode());
        return response;
    }

    public List<ViewStatsDto> get(ViewStatsParamDto paramDto) {
        log.info("Stats-client получен запрос на получение статистики с параметрами: {}", paramDto);
        ResponseEntity<List<ViewStatsDto>> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", paramDto.getStart().format(FORMATTER))
                        .queryParam("end", paramDto.getEnd().format(FORMATTER))
                        .queryParamIfPresent("uris",
                                Optional.ofNullable(
                                        CollectionUtils.isEmpty(paramDto.getUris()) ? null : paramDto.getUris()))
                        .queryParam("unique", paramDto.getUnique())
                        .build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        HttpStatusCode status = response.getStatusCode();
        List<ViewStatsDto> body = response.getBody();
        log.info("Stats-server ответил на запрос статистики от Stats-client, статус ответа: {}:", status);
        return body;
    }
}
