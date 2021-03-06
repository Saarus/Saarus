package org.saarus.mahout.classifier.sgd;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mahout.classifier.AbstractVectorClassifier;
import org.apache.mahout.classifier.sgd.ModelDissector;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.classifier.sgd.RecordFactory;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.io.Closeables;
import com.google.common.io.Resources;

public class TrainLogisticTest extends org.apache.mahout.common.MahoutTestCase {
  static String DONUT_CSV = "src/test/resources/donut/donut.csv" ;
  static String DONUT_TEST_CSV = "src/test/resources/donut/donut-test.csv" ;
  @Test
  public void example13_1() throws Exception {
    String outputFile = getTestTempFile("model").getAbsolutePath();
    System.out.println("Output File: " + outputFile) ;
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    TrainLogistic tl = new TrainLogistic() ;
    tl.train(new String[]{
        "--input", DONUT_CSV,
        "--output", outputFile,
        "--target", "color", 
        "--categories", "2",
        "--predictors", "numeric:x|numeric:y",
        "--features", "20",
        "--passes", "100",
        "--rate", "50"
    }, pw);
    String trainOut = sw.toString();
    assertTrue(trainOut.contains("x -0.7"));
    assertTrue(trainOut.contains("y -0.4"));

    LogisticModelParameters lmp = tl.getParameters();
    assertEquals(1.0e-4, lmp.getLambda(), 1.0e-9);
    assertEquals(20, lmp.getNumFeatures());
    assertTrue(lmp.useBias());
    assertEquals("color", lmp.getTargetVariable());
    RecordFactoryImpl csv = lmp.getCsvRecordFactory();
    assertEquals("[1, 2]", Sets.newTreeSet(csv.getTargetCategories()).toString());
    assertEquals("[Intercept Term, x, y]", Sets.newTreeSet(csv.getPredictors()).toString());

    // verify model by building dissector
    AbstractVectorClassifier model = tl.getModel();
    List<String> data = Resources.readLines(new URL("file:" + DONUT_CSV), Charsets.UTF_8);
    Map<String, Double> expectedValues = ImmutableMap.of("x", -0.7, "y", -0.43, "Intercept Term", -0.15);
    verifyModel(lmp, csv, data, model, expectedValues);

    // test saved model
    InputStream in = new FileInputStream(new File(outputFile));
    try {
      LogisticModelParameters lmpOut = LogisticModelParameters.loadFrom(in);
      RecordFactoryImpl csvOut = lmpOut.getCsvRecordFactory();
      csvOut.firstLine(data.get(0));
      OnlineLogisticRegression lrOut = lmpOut.createRegression();
      verifyModel(lmpOut, csvOut, data, lrOut, expectedValues);
    } finally {
      Closeables.closeQuietly(in);
    }

    sw = new StringWriter();
    pw = new PrintWriter(sw, true);
    String[] predictArgs = new String[]{
        "--input", DONUT_CSV,
        "--model", outputFile,
        "--auc", "--confusion"
    };
    new RunLogistic().predict(predictArgs, pw);
    trainOut = sw.toString();
    assertTrue(trainOut.contains("AUC = 0.57"));
    assertTrue(trainOut.contains("confusion: [[27.0, 13.0], [0.0, 0.0]]"));
  }

  @Test
  public void example13_2() throws Exception {
    String outputFile = "target/donut.model";
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw, true);
    TrainLogistic tl = new TrainLogistic() ;
    tl.train(new String[]{
        "--input", DONUT_CSV,
        "--output", outputFile,
        "--target", "color",
        "--categories", "2",
        "--predictors", "n:x|n:y|n:a|n:b|n:c",
        "--features", "20",
        "--passes", "100",
        "--rate", "50"
    }, pw);

    String trainOut = sw.toString();
    assertTrue(trainOut.contains("a 0."));
    assertTrue(trainOut.contains("b -1."));
    assertTrue(trainOut.contains("c -25."));

    sw = new StringWriter();
    pw = new PrintWriter(sw, true);
    
    new RunLogistic().predict(new String[]{
        "--input", DONUT_CSV,
        "--model", outputFile,
        "--auc",
        "--confusion", 
    }, pw);
    trainOut = sw.toString();
    assertTrue(trainOut.contains("AUC = 1.00"));

    sw = new StringWriter();
    pw = new PrintWriter(sw, true);
    String[] predictArgs = new String[]{
        "--input", DONUT_TEST_CSV,
        "--model", outputFile,
        "--auc",
        "--confusion"
    };
    new RunLogistic().predict(predictArgs, pw);
    trainOut = sw.toString();
    assertTrue(trainOut.contains("AUC = 0.9"));
  }

  private static void verifyModel(LogisticModelParameters lmp, RecordFactory csv, List<String> data,
                                  AbstractVectorClassifier model, Map<String, Double> expectedValues) {
    ModelDissector md = new ModelDissector();
    for (String line : data.subList(1, data.size())) {
      Vector v = new DenseVector(lmp.getNumFeatures());
      csv.getTraceDictionary().clear();
      csv.processLine(line, v);
      md.update(v, csv.getTraceDictionary(), model);
    }

    // check right variables are present
    List<ModelDissector.Weight> weights = md.summary(10);
    Set<String> expected = Sets.newHashSet(expectedValues.keySet());
    for (ModelDissector.Weight weight : weights) {
      assertTrue(expected.remove(weight.getFeature()));
      assertEquals(expectedValues.get(weight.getFeature()), weight.getWeight(), 0.1);
    }
    assertEquals(0, expected.size());
  }
}
