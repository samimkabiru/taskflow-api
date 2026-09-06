package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.auth.UserDto;
import com.theninjadev.taskflowapi.dtos.user.UpdateProfileRequest;
import com.theninjadev.taskflowapi.services.RefreshCookieService;
import com.theninjadev.taskflowapi.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final RefreshCookieService refreshCookieService;

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
    public ResponseEntity<byte[]> getAvatar(@PathVariable UUID userId) {
        var result = userService.getAvatar(userId);
        return ResponseEntity.ok()
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
