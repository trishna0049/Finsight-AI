package com.finsight.platform.dto.request;

import jakarta.validation.constraints.NotNull;

public record IncidentAssignRequest(@NotNull Long assigneeUserId) {
}
