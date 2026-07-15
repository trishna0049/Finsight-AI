package com.finsight.platform.service;

import com.finsight.platform.dto.request.LoginRequest;
import com.finsight.platform.dto.request.RefreshTokenRequest;
import com.finsight.platform.dto.response.AuthTokensResponse;
import com.finsight.platform.dto.response.UserProfileResponse;

public interface AuthService {
    AuthTokensResponse login(LoginRequest request);

    AuthTokensResponse refresh(RefreshTokenRequest request);

    void logout(String refreshToken);

    UserProfileResponse profile(String username);
}
