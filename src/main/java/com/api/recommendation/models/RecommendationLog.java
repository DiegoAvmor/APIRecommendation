package com.api.recommendation.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_recommendations_log")
public class RecommendationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Integer id;
    
    @Column(name = "id_user")
    private int idUser;

    @Column(name = "id_video")
    private int video;

    @Column(name = "log_date", updatable = false)
    private LocalDateTime logDate;

}
