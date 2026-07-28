package com.theninjadev.taskflowapi.controllers;

import com.theninjadev.taskflowapi.config.JwtConfig;
import com.theninjadev.taskflowapi.dtos.auth.AuthResponse;
import com.theninjadev.taskflowapi.dtos.auth.RegisterRequest;
import com.theninjadev.taskflowapi.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final JwtConfig jwtConfig;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        var authResult = authService.registerUser(request);

        var cookie = ResponseCookie.from("refreshToken", authResult.refreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/auth/refresh")
                .maxAge(jwtConfig.getRefreshTokenExpiration())
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AuthResponse(
                        authResult.accessToken(),
                        authResult.user()));
    }
}
