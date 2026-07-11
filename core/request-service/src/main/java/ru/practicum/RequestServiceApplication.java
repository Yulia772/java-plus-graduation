package ru.practicum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.interactionapi.client.EventClient;
import ru.practicum.interactionapi.client.UserClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {
        UserClient.class,
        EventClient.class
})
public class RequestServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RequestServiceApplication.class, args);
    }
}
