package com.finsight.platform.dto.response;

public record HeatmapPointResponse(
        int day,
        int hour,
        long count
) {
}
