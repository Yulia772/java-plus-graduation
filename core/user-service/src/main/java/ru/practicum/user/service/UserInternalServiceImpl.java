package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.interactionapi.dto.user.UserShortDto;
import ru.practicum.interactionapi.exception.NotFoundException;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserInternalServiceImpl implements UserInternalService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void checkUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
    }

    @Override
    public UserShortDto getUserShortDto(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        return userMapper.toUserShortDto(user);
    }

    @Override
    public List<UserShortDto> getUserShortDtos(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        List<User> users = userRepository.findAllById(ids);
        Set<Long> uniqueIds = new HashSet<>(ids);
        if (users.size() != uniqueIds.size()) {
            throw new NotFoundException("В списке есть несуществующие пользователи");
        }

        return users.stream()
                .map(userMapper::toUserShortDto)
                .toList();
    }
}
