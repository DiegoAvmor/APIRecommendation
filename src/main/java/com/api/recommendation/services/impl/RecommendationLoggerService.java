package com.api.recommendation.services.impl;

import java.util.List;

import com.api.recommendation.models.RecommendationLog;
import com.api.recommendation.repository.RecommendationLogRepository;
import com.api.recommendation.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecommendationLoggerService implements LogService<RecommendationLog>{

    @Autowired
    RecommendationLogRepository recommendationLogRepository;

    @Override
    public void saveList(List<RecommendationLog> objects) {
        recommendationLogRepository.saveAll(objects);
    }

    
}
