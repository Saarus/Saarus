package org.saarus.mahout;

import org.apache.mahout.classifier.sgd.RunLogistic ;

import org.junit.Test;

public class TrainLogisticUnitTest {
  @Test
  public void test() throws Exception {
    String[] trainArgs = {
        "--passes", "100", 
        "--rate", "50", "--lambda",  "0.001",
        "--input", "src/test/resources/donut.csv",
        "--features", "21", 
        "--output", "target/donut.model",
        "--target", "color",
        "--categories", "2",
        "--predictors", "x", "y", "xx", "xy", "yy", "a", "b", "c", 
        "--types", "n", "n"
    };
    TrainLogistic.main(trainArgs) ;
    String[] predictArgs = {
        "--model", "target/donut.model", "--auc",  "--scores", "--confusion",
        "--input", "src/test/resources/donut-test.csv"
    } ;
    RunLogistic.main(predictArgs) ;
  }
}
