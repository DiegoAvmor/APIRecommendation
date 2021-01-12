package com.api.recommendation.services.impl;

import com.api.recommendation.models.User;
import com.api.recommendation.models.UserRating;
import com.api.recommendation.models.UserRecommendation;
import com.api.recommendation.repository.RatingRepository;
import com.api.recommendation.repository.RecommendationRepository;
import com.api.recommendation.repository.UserRepository;
import com.api.recommendation.services.RecommendationService;
import com.opencsv.CSVWriter;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
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

    @Override
    @Scheduled(cron = "${cron.expression}")
    public void updateUsersRecommendations() {
        logger.info("Cron Task Executed");
        //Se hace la generación de los data sets
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String fileName = String.valueOf(timestamp.getTime());
        List<UserRating> ratings = ratingRepositoy.findAll();
        generateDataSetCSV(ratings, fileName);

        //Se realiza la actualización de las recomendaciones de los usuarios
        recommendationRepository.deleteAll();

        try{
            DataModel model = new FileDataModel(new File("src/main/resources/data-sets/" +fileName+".csv"));
            CityBlockSimilarity similarity = new CityBlockSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1,similarity, model);
            UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

            //Se realiza el eliminado de los registro de la tabla user_recommendations

            
            List<User> users = userRepository.findAll();
            List<UserRecommendation> newRecommendations = new ArrayList();
            for (User user : users) {
                //Si existe procedemos a obtener sus recomendaciones
                if(ratingRepositoy.existsByIdUser(user.getId())){
                    logger.info("Usuario ID: " + user.getId());
                    // The First argument is the userID and the Second parameter is 'HOW MANY
                    List<RecommendedItem> recommendations = recommender.recommend(user.getId(), 3);
                    for (RecommendedItem recommendation : recommendations) {
                        UserRecommendation userRecommendation = new UserRecommendation();
                        userRecommendation.setIdUser(user.getId());
                        userRecommendation.setVideo((int)recommendation.getItemID());
                        recommendationRepository.save(userRecommendation);
                        System.out.println(recommendation);
                    }      

                }
            }
            if(!newRecommendations.isEmpty()){
                recommendationRepository.saveAll(newRecommendations);
            }
		} catch (Exception e) {
            e.printStackTrace();
		}
    }
    
    private void generateDataSetCSV(List<UserRating> ratings, String filename){
        try {
            CSVWriter writer = new CSVWriter(new FileWriter("src/main/resources/data-sets/" +filename+".csv"),
            CSVWriter.DEFAULT_SEPARATOR,
            CSVWriter.NO_QUOTE_CHARACTER,
            CSVWriter.NO_ESCAPE_CHARACTER,
            CSVWriter.DEFAULT_LINE_END);

            for (UserRating userRating : ratings) {
                writer.writeNext(userRating.getRatingData());
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
