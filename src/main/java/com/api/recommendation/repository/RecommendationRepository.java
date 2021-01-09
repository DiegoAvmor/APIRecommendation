package com.api.recommendation.repository;

import com.api.recommendation.models.UserRecommendation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationRepository extends JpaRepository<UserRecommendation,Integer>{
    
}
