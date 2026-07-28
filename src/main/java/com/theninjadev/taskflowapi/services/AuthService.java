package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.config.JwtConfig;
import com.theninjadev.taskflowapi.dtos.auth.AuthResult;
import com.theninjadev.taskflowapi.dtos.auth.LoginRequest;
import com.theninjadev.taskflowapi.dtos.auth.RegisterRequest;
import com.theninjadev.taskflowapi.entities.RefreshToken;
import com.theninjadev.taskflowapi.entities.User;
import com.theninjadev.taskflowapi.exceptions.InvalidCredentialsException;
import com.theninjadev.taskflowapi.exceptions.InvalidRefreshTokenException;
import com.theninjadev.taskflowapi.exceptions.UserExistsException;
import com.theninjadev.taskflowapi.mappers.UserMapper;
import com.theninjadev.taskflowapi.repositories.RefreshTokenRepository;
import com.theninjadev.taskflowapi.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@AllArgsConstructor
public class AuthService {
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

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
            throw new InvalidCredentialsException();

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
