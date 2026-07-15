package com.finsight.platform.service.impl;

import com.finsight.platform.service.AiAnalysisService;
import com.finsight.platform.service.impl.model.AiAnalysisRequest;
import com.finsight.platform.service.impl.model.AiAnalysisResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AiAnalysisServiceImpl implements AiAnalysisService {

    private final RestClient restClient;
    private final String aiBaseUrl;

    public AiAnalysisServiceImpl(RestClient restClient, @Value("${app.ai.base-url}") String aiBaseUrl) {
        this.restClient = restClient;
        this.aiBaseUrl = aiBaseUrl;
    }

    @Override
    public AiAnalysisResult analyze(AiAnalysisRequest request) {
        return restClient.post()
                .uri(aiBaseUrl + "/api/v1/analysis/incident")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(AiAnalysisResult.class);
    }
}
