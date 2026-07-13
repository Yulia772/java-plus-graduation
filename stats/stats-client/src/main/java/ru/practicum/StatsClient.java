package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.request.ViewStatsParamDto;
import ru.practicum.exceptions.StatsClientException;

import ru.practicum.dto.request.EndPointHitDtoNew;
import ru.practicum.dto.response.ViewStatsDto;
import ru.practicum.exceptions.StatsServerUnavailable;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.net.URI;

@Slf4j
@Component
public class StatsClient {
    private static final String STATS_SERVICE_ID = "stats-server";

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestClient restClient;
    private final DiscoveryClient discoveryClient;

    public StatsClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
        this.restClient = RestClient.builder()
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
                .uri(makeUri("/hit"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitDto)
                .retrieve()
                .toBodilessEntity();
        log.info("Stats-server ответил на запрос Stats-client, статус ответа: {}", response.getStatusCode());
        return response;
    }

    public List<ViewStatsDto> get(ViewStatsParamDto paramDto) {
        log.info("Stats-client получен запрос на получение статистики с параметрами: {}", paramDto);
        URI uri  = UriComponentsBuilder.fromUri(makeUri("/stats"))
                .queryParam("start", paramDto.getStart().format(FORMATTER))
                .queryParam("end", paramDto.getEnd().format(FORMATTER))
                .queryParamIfPresent("uris",
                        Optional.ofNullable(
                                CollectionUtils.isEmpty(paramDto.getUris()) ? null : paramDto.getUris()))
                .queryParam("unique", paramDto.getUnique())
                .build()
                .encode().toUri();

        ResponseEntity<List<ViewStatsDto>> response = restClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<>() {});

        HttpStatusCode status = response.getStatusCode();
        List<ViewStatsDto> body = response.getBody();

        log.info("Stats-server ответил на запрос статистики от Stats-client, статус ответа: {}:", status);
        return body;
    }

    private URI makeUri(String path) {
        ServiceInstance instance = getInstance();
        return URI.create("http://" + instance.getHost() + ":" + instance.getPort() + path);
    }

    private ServiceInstance getInstance() {
        try {
            return discoveryClient
                    .getInstances(STATS_SERVICE_ID)
                    .getFirst();
        } catch (Exception exception) {
            throw new StatsServerUnavailable(
                    "Ошибка обнаружения адреса сервиса статистики с id: " + STATS_SERVICE_ID,
                    exception
            );
        }
    }
}
