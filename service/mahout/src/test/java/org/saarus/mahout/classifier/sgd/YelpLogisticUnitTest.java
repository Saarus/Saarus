package org.saarus.mahout.classifier.sgd;

import java.io.PrintWriter;

import org.junit.Test;

public class YelpLogisticUnitTest {
  @Test
  public void test() throws Exception {
    String[] trainArgs = {
        "--passes", "50", 
        "--rate", "50", "--lambda",  "0.001",
        "--input", "src/test/resources/review-training.csv",
        "--features", "10", 
        "--output", "target/review.model",
        "--target", "vote_useful", /*"vote_funny",*//*"n:vote_useful",*//*"vote_cool",*/
        "--categories", "2",
        "--predictors", "n:stars", /*"n:text",*/ /*"w:business_id",*/ 
                        /*"w:business_city", "w:business_state",*/ /*"w:business_open",*/ "n:business_review_count",
                        /*"n:user_review_count",*/ /*"n:user_average_stars"*/ 
    };
    TrainLogistic tl = new TrainLogistic() ;
    tl.train(trainArgs, new PrintWriter(System.out, true)) ;

    String[] predictArgs = {
        "--model", "target/review.model", "--scores", "--auc", "--confusion",
        "--input", "src/test/resources/review-test.csv"
    } ;
    new RunLogistic().predict(predictArgs, new PrintWriter(System.out, true)) ;
  }
}
