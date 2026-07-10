package ru.practicum.interactionapi.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.interactionapi.client.config.CommonFeignConfig;
import ru.practicum.interactionapi.dto.user.UserShortDto;

import java.util.List;

@FeignClient(name = "user-service", configuration = CommonFeignConfig.class)
public interface UserClient {

    @GetMapping("/internal/users/{userId}/exists")
    void checkUserExists(@PathVariable Long userId);

    @GetMapping("/internal/users/{userId}/short")
    UserShortDto getUserShortDto(@PathVariable Long userId);

    @GetMapping("/internal/users/short")
    List<UserShortDto> getUserShortDtos(@RequestParam("ids") List<Long> ids);
}
