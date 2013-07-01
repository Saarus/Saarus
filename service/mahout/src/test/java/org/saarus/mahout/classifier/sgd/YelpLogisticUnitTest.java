package org.saarus.mahout.classifier.sgd;

import java.io.PrintWriter;

import org.junit.Test;
import org.saarus.service.sql.SQLService;

public class YelpLogisticUnitTest {
  @Test
  public void test() throws Exception {
    String modelFile = "dfs:/tmp/review-features.model" ;
    String[] trainArgs = {
        "--passes", "50", 
        "--rate", "50", "--lambda",  "0.001",
        "--input",  "src/test/resources/review-training.csv",//"src/test/resources/review-training.csv", hive://features
        "--features", "20", 
        "--output", modelFile,
        "--target", "vote_useful", /*"vote_funny",*//*"n:vote_useful",*//*"vote_cool",*/
        "--categories", "2",
        "--noBias",
        "--predictors", "n:stars | n:business_review_count",
    };
    SQLService hservice  = new SQLService("jdbc:hive2://198.154.60.252:10000", "hive", "");
    TrainLogistic tl = new TrainLogistic().setHiveService(hservice) ;
    tl.train(trainArgs, new PrintWriter(System.out, true)) ;

    String[] predictArgs = {
        "--model", modelFile, "--scores", "--auc", "--confusion",
        "--input", "src/test/resources/review-test.csv"
    } ;
    new RunLogistic().predict(predictArgs, new PrintWriter(System.out, true)) ;
  }
}
