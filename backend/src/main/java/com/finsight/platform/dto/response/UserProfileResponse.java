package com.finsight.platform.dto.response;

import java.util.Set;

public record UserProfileResponse(
        String username,
        String fullName,
        String email,
        Set<String> roles
) {
}
