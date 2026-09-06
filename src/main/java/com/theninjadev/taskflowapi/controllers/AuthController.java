package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.dtos.auth.*;
import com.theninjadev.taskflowapi.services.AuthService;
import com.theninjadev.taskflowapi.services.RefreshCookieService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final RefreshCookieService refreshCookieService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        var authResult = authService.registerUser(request);

        var cookie = refreshCookieService.buildRefreshTokenCookie(authResult.refreshToken());

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AuthResponse(
                        authResult.accessToken(),
                        authResult.user()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {

        var authResult = authService.loginUser(request);

        var cookie = refreshCookieService.buildRefreshTokenCookie(authResult.refreshToken());

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity
                .ok(new AuthResponse(
                        authResult.accessToken(),
                        authResult.user()));

    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> loginWithGoogle(
            @Valid @RequestBody GoogleLoginRequest request,
            HttpServletResponse response
            ) {
        var authResult = authService.loginWithGoogle(request);

        var cookie = refreshCookieService.buildRefreshTokenCookie(authResult.refreshToken());

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity
                .ok(new AuthResponse(
                        authResult.accessToken(),
                        authResult.user()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        authService.logoutUser(refreshToken);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookieService.clearRefreshTokenCookie().toString());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletResponse response
    ) {
        authService.changeUserPassword(request);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookieService.clearRefreshTokenCookie().toString());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshTokens(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response) {
        var authResult = authService.refreshUserTokens(refreshToken);

        var cookie = refreshCookieService.buildRefreshTokenCookie(authResult.refreshToken());
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity
                .ok(new AuthResponse(
                        authResult.accessToken(),
                        authResult.user()));
    }
}
