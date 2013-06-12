package org.saarus.mahout;

import org.apache.mahout.classifier.sgd.TrainLogistic ;
import org.apache.mahout.classifier.sgd.RunLogistic ;

import org.junit.Test;

public class YelpLogisticUnitTest {
  @Test
  public void test() throws Exception {
    String[] trainArgs = {
        "--passes", "100", 
        "--rate", "50", "--lambda",  "0.001",
        "--input", "src/test/resources/review-training.csv",
        "--features", "2", 
        "--output", "target/review.model",
        "--target", "vote_useful",
        "--categories", "2",
        //"--predictors", "review_id", "stars", "text", "vote_funny","vote_useful","vote_cool", "business_id", "business_city", "business_state", "business_open", "business_review_count", "user_id", 
        //"--types",      "w",         "n",     "n",    "n",         "n",          "n",         "w",           "w",             "w",              "w",             "n",                     "w"           
        "--predictors", "stars", "text", /*"vote_funny",*/"vote_useful",/*"vote_cool",*/ /*"business_id",*/ "business_city", "business_state", "business_open", "business_review_count","user_review_count", "user_average_stars", 
        "--types",      "n",     "n",    /*"n",*/         "n",          /*"n",*/         /*"w",*/           "w",             "w",              "w",             "n",                    "n",                 "n"
    };
    TrainLogistic.main(trainArgs) ;
    String[] predictArgs = {
        "--model", "target/review.model", "--scores", "--auc", "--confusion",
        "--input", "src/test/resources/review-test.csv"
    } ;
    RunLogistic.main(predictArgs) ;
  }
}
