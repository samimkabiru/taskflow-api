package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.dtos.auth.UserDto;
import com.theninjadev.taskflowapi.dtos.user.GetAvatarResult;
import com.theninjadev.taskflowapi.dtos.user.UpdateProfileRequest;
import com.theninjadev.taskflowapi.exceptions.*;
import com.theninjadev.taskflowapi.mappers.UserMapper;
import com.theninjadev.taskflowapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final FileStorageService fileStorageService;
    @Value("${app.avatar.max-size}")
    private DataSize maxImageSize;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto updateProfile(UUID currentUserId, UpdateProfileRequest request) {
        var user = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);

        if (request.getFullName() != null)
            user.setFullName(request.getFullName());

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public UserDto uploadAvatar(UUID currentUserId, MultipartFile file) {
        var user = userRepository.findById(currentUserId).orElseThrow(UserNotFoundException::new);

        if (file.isEmpty())
            throw new EmptyFileException();

        if (file.getSize() > maxImageSize.toBytes())
            throw new FileTooLargeException();

        if (!Set.of("image/jpeg", "image/png", "image/webp").contains(file.getContentType()))
            throw new InvalidImageTypeException();

        if (user.getAvatarUrl() != null)
            fileStorageService.delete(user.getAvatarUrl());

        var storageKey = fileStorageService.store(file);
        user.setAvatarUrl(storageKey);
        user.setAvatarContentType(file.getContentType());

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public GetAvatarResult getAvatar(UUID userId) {
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (user.getAvatarUrl() == null)
            throw new AttachmentNotFoundException();

        var bytes = fileStorageService.load(user.getAvatarUrl());

        return new GetAvatarResult(bytes, user.getAvatarContentType());
    }
}
