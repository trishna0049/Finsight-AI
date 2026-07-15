package com.finsight.platform.dto.response;

public record ServiceFailureResponse(
        String service,
        long incidents
) {
}
