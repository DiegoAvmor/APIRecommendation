package com.api.recommendation.repository;
import java.util.List;

import com.api.recommendation.models.UserRating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<UserRating,Integer>{

    boolean existsByIdUser(Integer id);

    List<UserRating> findByIdUser(Integer userId);
    
}