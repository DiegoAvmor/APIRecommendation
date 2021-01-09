package com.api.recommendation.repository;
import com.api.recommendation.models.UserRating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<UserRating,Integer>{
    
}