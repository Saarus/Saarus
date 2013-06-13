package org.saarus.mahout.classifier.sgd;

import java.io.PrintWriter;

import org.junit.Test;
import org.saarus.service.hive.HiveService;

public class YelpLogisticUnitTest {
  @Test
  public void test() throws Exception {
    String[] trainArgs = {
        "--passes", "50", 
        "--rate", "50", "--lambda",  "0.001",
        "--input",  "hive://features",//"src/test/resources/review-training.csv",
        "--features", "20", 
        "--output", "target/review.model",
        "--target", "vote_useful", /*"vote_funny",*//*"n:vote_useful",*//*"vote_cool",*/
        "--categories", "2",
        "--predictors", "n:stars|n:business_review_count",
    };
    HiveService hservice  = new HiveService("jdbc:hive2://198.154.60.252:10000", "hive", "");
    TrainLogistic tl = new TrainLogistic().setHiveService(hservice) ;
    tl.train(trainArgs, new PrintWriter(System.out, true)) ;

    String[] predictArgs = {
        "--model", "target/review.model", "--scores", "--auc", "--confusion",
        "--input", "src/test/resources/review-test.csv"
    } ;
    new RunLogistic().predict(predictArgs, new PrintWriter(System.out, true)) ;
  }
}
