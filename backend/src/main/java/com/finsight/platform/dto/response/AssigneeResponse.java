package com.finsight.platform.dto.response;

public record AssigneeResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String role
) {
}
