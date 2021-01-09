package com.api.recommendation.services;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

public interface RecommendationService {

    void updateUsersRecommendations();
    
}
