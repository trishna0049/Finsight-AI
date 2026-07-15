package com.finsight.platform.service;

import com.finsight.platform.service.impl.model.AiAnalysisRequest;
import com.finsight.platform.service.impl.model.AiAnalysisResult;

public interface AiAnalysisService {
    AiAnalysisResult analyze(AiAnalysisRequest request);
}
