package com.finsight.platform.dto.request;

import jakarta.validation.constraints.NotBlank;

public record IncidentStatusUpdateRequest(@NotBlank String status) {
}
