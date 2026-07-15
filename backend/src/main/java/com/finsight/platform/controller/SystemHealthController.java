package com.finsight.platform.controller;

import com.finsight.platform.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/system")
public class SystemHealthController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> payload = Map.of(
                "status", "UP",
                "service", "finsight-backend",
                "timestamp", Instant.now().toString()
        );

        return ResponseEntity.ok(ApiResponse.success(payload));
    }
}
