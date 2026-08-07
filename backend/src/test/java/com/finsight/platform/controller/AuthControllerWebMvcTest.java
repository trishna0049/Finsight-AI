package com.finsight.platform.controller;

import com.finsight.platform.dto.response.AuthTokensResponse;
import com.finsight.platform.dto.response.UserProfileResponse;
import com.finsight.platform.security.JwtService;
import com.finsight.platform.security.PlatformUserDetailsService;
import com.finsight.platform.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

        @MockBean
        private JwtService jwtService;

        @MockBean
        private PlatformUserDetailsService platformUserDetailsService;

    @Test
    void loginShouldReturnAccessAndRefreshTokens() throws Exception {
        when(authService.login(any())).thenReturn(new AuthTokensResponse(
                "access-token",
                "refresh-token",
                "Bearer",
                1800
        ));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username":"incident.analyst",
                                  "password":"password"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token"));
    }

    @Test
    void profileShouldReturnAuthenticatedUserProfile() throws Exception {
        when(authService.profile(eq("incident.analyst"))).thenReturn(new UserProfileResponse(
                "incident.analyst",
                "Incident Analyst",
                "incident.analyst@finsight.local",
                Set.of("ROLE_INCIDENT_ANALYST")
        ));

        mockMvc.perform(get("/api/v1/auth/profile")
                        .principal(new TestingAuthenticationToken("incident.analyst", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("incident.analyst"));
    }

    @Test
    void refreshShouldReturnNewAccessAndRefreshTokens() throws Exception {
        when(authService.refresh(any())).thenReturn(new AuthTokensResponse(
                "new-access-token",
                "new-refresh-token",
                "Bearer",
                1800
        ));

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken":"refresh-token"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"));
    }

    @Test
    void logoutShouldReturnSuccess() throws Exception {
        doNothing().when(authService).logout(eq("refresh-token"));

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken":"refresh-token"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
