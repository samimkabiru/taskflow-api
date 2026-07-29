package com.theninjadev.taskflowapi.services;

import com.theninjadev.taskflowapi.config.JwtConfig;
import com.theninjadev.taskflowapi.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
public class JwtService {
    private final JwtConfig jwtConfig;

    public String generateAccessToken(User user) {
        return generateToken(user, jwtConfig.getAccessTokenExpiration(), "access");
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, jwtConfig.getRefreshTokenExpiration(), "refresh");
    }

    public String generateToken(User user, long tokenExpiration, String type) {

        return  Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("type", type)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtConfig.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isExpired(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public String getTypeOfToken(String token) {
        return getClaims(token).get("type").toString();
    }

    public UUID getSubject(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
