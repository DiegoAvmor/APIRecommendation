package com.api.recommendation.controller;

import com.api.recommendation.services.impl.RecommendationServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RecommendationController {
    @Autowired
    private RecommendationServiceImpl recommendationService;

    @GetMapping("/generate")
    public void generateRecommendations(){
        recommendationService.updateUsersRecommendations();
    }
    
}
