package ru.practicum.user.service;


import org.springframework.data.domain.Pageable;
import ru.practicum.interactionapi.dto.user.NewUserRequest;
import ru.practicum.interactionapi.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(NewUserRequest newUserRequest);

    List<UserDto> findUsers(Long[] ids, Pageable pageable);

    void deleteUser(Long id);
}