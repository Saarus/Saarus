package org.saarus.mahout.classifier.sgd;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.mahout.examples.MahoutTestCase;
import org.junit.Test;
import org.saarus.service.hive.HiveService;

public class TrainLogisticWithHiveUnitTest extends MahoutTestCase {
  static String DONUT_TEST_CSV = "src/test/resources/donut-test.csv" ;

  @Test
  public void test() throws Exception {
    String outputFile = "target/donut.model";
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    HiveService hservice  = new HiveService("jdbc:hive2://198.154.60.252:10000", "hive", "");
    TrainLogistic tl = new TrainLogistic().setHiveService(hservice) ;
    tl.train(new String[]{
        "--input", "hive://donut_train", // "src/test/resources/donut.csv" , "hive://donut_train"
        "--output", outputFile,
        "--target", "color",
        "--categories", "2",
        "--predictors", "n:x", "n:y", "n:a", "n:b", "n:c",
        "--features", "20",
        "--passes", "100",
        "--rate", "50"
    }, pw);
    
    String trainOut = sw.toString();
    System.out.println(trainOut);
    
    assertTrue(trainOut.contains("a 0."));
    assertTrue(trainOut.contains("b -1."));
    assertTrue(trainOut.contains("c -25."));

    sw = new StringWriter();
    pw = new PrintWriter(sw, true);
    
    new RunLogistic().predict(new String[]{
        "--input", DONUT_TEST_CSV,
        "--model", outputFile,
        "--auc", "--confusion", 
    }, pw);
    String predictOut = sw.toString();
    System.out.println(predictOut);
    assertTrue(predictOut.contains("AUC = 0.9"));
  }
}
