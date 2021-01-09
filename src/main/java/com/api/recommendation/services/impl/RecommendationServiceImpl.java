package com.api.recommendation.services.impl;

import com.api.recommendation.models.User;
import com.api.recommendation.models.UserRating;
import com.api.recommendation.repository.RatingRepository;
import com.api.recommendation.repository.UserRepository;
import com.api.recommendation.services.RecommendationService;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
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
import org.springframework.stereotype.Service;
import org.apache.mahout.cf.taste.impl.similarity.CityBlockSimilarity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
@Service
public class RecommendationServiceImpl implements RecommendationService{
    Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);

    @Autowired
    private RatingRepository ratingRepositoy;

    @Autowired
    private UserRepository userRpeository;

    @Override
    public void updateUsersRecommendations() {
        //Se hace la generación de los data sets
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        List<UserRating> ratings = ratingRepositoy.findAll();
        generateDataSetCSV(ratings, String.valueOf(timestamp.getTime()));
        

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
