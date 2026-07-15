package com.finsight.platform.service.impl;

import com.finsight.platform.domain.entity.RefreshToken;
import com.finsight.platform.domain.entity.User;
import com.finsight.platform.domain.enums.AuditAction;
import com.finsight.platform.dto.request.LoginRequest;
import com.finsight.platform.dto.request.RefreshTokenRequest;
import com.finsight.platform.dto.response.AuthTokensResponse;
import com.finsight.platform.dto.response.UserProfileResponse;
import com.finsight.platform.exception.ResourceNotFoundException;
import com.finsight.platform.repository.RefreshTokenRepository;
import com.finsight.platform.repository.UserRepository;
import com.finsight.platform.security.JwtService;
import com.finsight.platform.service.AuthService;
import com.finsight.platform.service.AuditService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuditService auditService;

    @Value("${app.security.jwt.refresh-token-days}")
    private long refreshTokenDays;

    @Value("${app.security.jwt.access-token-minutes}")
    private long accessTokenMinutes;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            AuditService auditService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public AuthTokensResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = generateRefreshToken(user);
        auditService.record(AuditAction.LOGIN, "User logged in", user.getUsername());
        return new AuthTokensResponse(accessToken, refreshToken, "Bearer", accessTokenMinutes * 60);
    }

    @Override
    @Transactional
    public AuthTokensResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByTokenAndRevokedFalse(request.refreshToken())
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found or revoked"));

        if (refreshToken.getExpiresAt().isBefore(OffsetDateTime.now())) {
            refreshToken.setRevoked(true);
            throw new ResourceNotFoundException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        UserDetails principal = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(user.getRoles().stream().map(r -> r.getName().name()).toArray(String[]::new))
                .build();

        String accessToken = jwtService.generateAccessToken(principal);
        auditService.record(AuditAction.LOGIN, "Access token refreshed", user.getUsername());
        return new AuthTokensResponse(accessToken, refreshToken.getToken(), "Bearer", accessTokenMinutes * 60);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByTokenAndRevokedFalse(refreshToken)
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                    auditService.record(AuditAction.LOGOUT, "Refresh token revoked", token.getUser().getUsername());
                });
    }

    @Override
    public UserProfileResponse profile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new UserProfileResponse(
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRoles().stream().map(r -> r.getName().name()).collect(java.util.stream.Collectors.toSet())
        );
    }

    private String generateRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID() + "." + UUID.randomUUID());
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(OffsetDateTime.now().plusDays(refreshTokenDays));
        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }
}
