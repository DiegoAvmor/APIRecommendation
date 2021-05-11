package com.api.recommendation.services.impl;

import com.api.recommendation.models.RecommendationLog;
import com.api.recommendation.models.User;
import com.api.recommendation.models.UserRating;
import com.api.recommendation.models.UserRecommendation;
import com.api.recommendation.repository.RatingRepository;
import com.api.recommendation.repository.RecommendationRepository;
import com.api.recommendation.repository.UserRepository;
import com.api.recommendation.services.RecommendationService;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@EnableScheduling
public class RecommendationServiceImpl implements RecommendationService{
    Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    @Autowired
    private RatingRepository ratingRepositoy;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private RecommendationLoggerService recommendationLoggerService;

    @Override
    @Scheduled(cron = "${cron.expression}")
    public void updateUsersRecommendations() {
        logger.info("Cron Task Executed");
        List<User> users = userRepository.findAll();

        //Se realiza la actualización de las recomendaciones de los usuarios
        recommendationRepository.deleteAll();

        try{
            FastByIDMap<PreferenceArray> userData = new FastByIDMap<PreferenceArray>();
            for (User user : users) {
                List<UserRating> userRatings = ratingRepositoy.findByIdUser(user.getId());
                List<GenericPreference> genericPreferences = new ArrayList<>();
                for (UserRating userRating : userRatings) {
                    GenericPreference preference = new GenericPreference(userRating.getIdUser(), userRating.getIdVideo(), userRating.getRating());
                    genericPreferences.add(preference);
                }
                userData.put(user.getId(), new GenericUserPreferenceArray(genericPreferences));
            }
            
            DataModel model = new GenericDataModel(userData);
            CityBlockSimilarity similarity = new CityBlockSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1,similarity, model);
            UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);


            LocalDateTime date = LocalDateTime.now();
            List<RecommendationLog> recommendationLogs = new ArrayList<>();
            List<UserRecommendation> newRecommendations = new ArrayList<>();
            for (User user : users) {
                //Si existe procedemos a obtener sus recomendaciones
                if(ratingRepositoy.existsByIdUser(user.getId())){
                    logger.info("Usuario ID: " + user.getId());
                    List<RecommendedItem> recommendations = recommender.recommend(user.getId(), 3);
                    for (RecommendedItem recommendation : recommendations) {
                        UserRecommendation userRecommendation = new UserRecommendation();
                        userRecommendation.setIdUser(user.getId());
                        userRecommendation.setVideo((int)recommendation.getItemID());
                        newRecommendations.add(userRecommendation);
                        //Se añade a la lista del historial de recommendaciones
                        RecommendationLog log = new RecommendationLog();
                        log.setIdUser(userRecommendation.getIdUser());
                        log.setVideo(userRecommendation.getVideo());
                        log.setLogDate(date);
                        recommendationLogs.add(log);
                        System.out.println(recommendation);
                    }      

                }
            }
            //Se añade las recommendaciones al historial
            recommendationLoggerService.saveList(recommendationLogs);
            //Se actualiza la tabla
            recommendationRepository.saveAll(newRecommendations);
            
		} catch (Exception e) {
            e.printStackTrace();
		}
    }
    
}
