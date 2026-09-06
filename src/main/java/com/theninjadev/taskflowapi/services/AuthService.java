package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.config.JwtConfig;
import com.theninjadev.taskflowapi.dtos.auth.*;
import com.theninjadev.taskflowapi.entities.RefreshToken;
import com.theninjadev.taskflowapi.entities.User;
import com.theninjadev.taskflowapi.enums.AuthProvider;
import com.theninjadev.taskflowapi.exceptions.*;
import com.theninjadev.taskflowapi.mappers.UserMapper;
import com.theninjadev.taskflowapi.repositories.RefreshTokenRepository;
import com.theninjadev.taskflowapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value( "${app.google.client-id}")
    private String googleClientId;

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    @Transactional
    public AuthResult registerUser(RegisterRequest request) {
        var email = request.getEmail().trim().toLowerCase();
        var userExists =  userRepository.existsByEmail(email);
        if (userExists)
            throw new UserExistsException();

        var user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return issueTokensFor(user);
    }

    @Transactional
    public AuthResult loginUser(LoginRequest request) {
        var email = request.getEmail().trim().toLowerCase();
        var user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
            throw new InvalidCredentialsException();

        return issueTokensFor(user);
    }

    public AuthResult loginWithGoogle(GoogleLoginRequest request) {
        RestClient restClient = RestClient.create();

        GoogleTokenInfo tokenInfo;
        try {
            tokenInfo = restClient.get()
                    .uri("https://oauth2.googleapis.com/tokeninfo?id_token={idToken}", request.getIdToken())
                    .retrieve()
                    .body(GoogleTokenInfo.class);
        } catch (Exception e) {
            throw new OAuthVerificationException();
        }

        if (tokenInfo.aud() == null || !tokenInfo.aud().equals(googleClientId))
            throw new OAuthVerificationException();

        if (!Boolean.parseBoolean(tokenInfo.email_verified()))
            throw new OAuthVerificationException();

        var email = tokenInfo.email();
        var user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            user = new User();
            user.setFullName(tokenInfo.name());
            user.setEmail(email);
            user.setAuthProvider(AuthProvider.GOOGLE);
            user.setPasswordHash(null);
            userRepository.save(user);
        }

        return issueTokensFor(user);
    }

    @Transactional
    public AuthResult refreshUserTokens(String refreshToken) {
        var existingToken = refreshTokenRepository
                .findByTokenHash(jwtService.hashToken(refreshToken))
                .orElseThrow(InvalidRefreshTokenException::new);

        if (existingToken.getRevoked())
            throw new InvalidRefreshTokenException();

        if (existingToken.getExpiresAt().isBefore(OffsetDateTime.now()))
            throw new InvalidRefreshTokenException();

        existingToken.setRevoked(true);
        refreshTokenRepository.save(existingToken);

        return issueTokensFor(existingToken.getUser());
    }

    public void logoutUser(String refreshToken) {
        var existingToken = refreshTokenRepository.findByTokenHash(jwtService.hashToken(refreshToken))
                .orElseThrow(InvalidRefreshTokenException::new);

        if (existingToken.getRevoked())
            throw new InvalidRefreshTokenException();

        existingToken.setRevoked(true);
        refreshTokenRepository.save(existingToken);

    }

    @Transactional
    public void changeUserPassword(ChangePasswordRequest request) {
        var userId = (UUID) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash()))
            throw new InvalidCredentialsException();

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        refreshTokenRepository.revokeAllByUserId(userId);
    }

    private AuthResult issueTokensFor(User user) {
        var token = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        var refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setTokenHash(jwtService.hashToken(refreshToken));
        refreshTokenEntity.setExpiresAt(OffsetDateTime.now().plusSeconds(jwtConfig.getRefreshTokenExpiration()));
        refreshTokenEntity.setRevoked(false);

        refreshTokenRepository.save(refreshTokenEntity);

        return new AuthResult(token, refreshToken, userMapper.toDto(user));
    }
}
