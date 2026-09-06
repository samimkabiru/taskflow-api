package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.auth.UserDto;
import com.theninjadev.taskflowapi.dtos.user.UpdateProfileRequest;
import com.theninjadev.taskflowapi.exceptions.UserNotFoundException;
import com.theninjadev.taskflowapi.repositories.UserRepository;
import com.theninjadev.taskflowapi.services.RefreshCookieService;
import com.theninjadev.taskflowapi.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final RefreshCookieService refreshCookieService;
    private final UserRepository userRepository;

    @PatchMapping("/me")
    public ResponseEntity<UserDto> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(userService.updateProfile(getCurrentUserId(), request));
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<UserDto> uploadAvatar(
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(userService.uploadAvatar(getCurrentUserId(), file));
    }

    @GetMapping("/{userId}/avatar")
    public ResponseEntity<byte[]> getAvatar(@PathVariable UUID userId, WebRequest request) {
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        var etag = "\"" + user.getAvatarUrl() + "\"";

        if (request.checkNotModified(etag)) {
            return null;
        }

        var result = userService.getAvatar(userId);
        return ResponseEntity.ok()
                .eTag(etag)
                .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS).cachePublic())
                .contentType(MediaType.parseMediaType(result.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(result.bytes());
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAccount(HttpServletResponse response) {
        userService.deleteAccount(getCurrentUserId());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookieService.clearRefreshTokenCookie().toString());
        return ResponseEntity.noContent().build();
    }

    private UUID getCurrentUserId() {
        return (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
