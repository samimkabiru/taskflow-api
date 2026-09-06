package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.auth.UserDto;
import com.theninjadev.taskflowapi.dtos.user.UpdateProfileRequest;
import com.theninjadev.taskflowapi.exceptions.UserNotFoundException;
import com.theninjadev.taskflowapi.mappers.UserMapper;
import com.theninjadev.taskflowapi.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto updateProfile(UUID currentUserId, UpdateProfileRequest request) {
        var user = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);

        if (request.getFullName() != null)
            user.setFullName(request.getFullName());

        userRepository.save(user);

        return userMapper.toDto(user);
    }
}
