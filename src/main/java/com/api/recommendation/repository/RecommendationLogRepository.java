package com.api.recommendation.repository;

import com.api.recommendation.models.RecommendationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationLogRepository extends JpaRepository<RecommendationLog,Integer>{
}
