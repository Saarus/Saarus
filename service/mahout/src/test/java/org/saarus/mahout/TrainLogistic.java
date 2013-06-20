package org.saarus.mahout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import org.apache.mahout.classifier.CsvRecordFactory;
import org.apache.mahout.classifier.sgd.LogisticModelParameters;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.classifier.sgd.RecordFactory;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import com.google.common.io.Resources;

/**
 * Train a logistic regression for the examples from Chapter 13 of Mahout in Action
 */
public final class TrainLogistic {

  private String                   inputFile;
  private String                   outputFile;
  private LogisticModelParameters  lmp;
  private int                      passes;
  private boolean                  scores;
  private OnlineLogisticRegression model;

  private TrainLogistic() {
  }

  public void run(String[] args) throws Exception {
    PrintWriter output = new PrintWriter(System.out, true) ;
    this.inputFile =  "src/test/resources/donut.csv" ;
    this.outputFile = "target/donut.model";

    List<String> typeList = Lists.newArrayList("n", "n");
    List<String> predictorList = Lists.newArrayList("x", "y", "xx", "xy", "yy", "a", "b", "c");

    lmp = new LogisticModelParameters();
    lmp.setTargetVariable("color");
    lmp.setMaxTargetCategories(2/*categories*/);
    lmp.setNumFeatures(21 /*features*/);
    lmp.setUseBias(true /*noBias*/);
    lmp.setTypeMap(predictorList, typeList);

    lmp.setLambda(0.001 /*lampda*/) ;
    lmp.setLearningRate(50 /*rate*/);

    this.scores = false; 
    this.passes = 100; 

    double logPEstimate = 0;
    int    samples = 0;

    CsvRecordFactory csv = lmp.getCsvRecordFactory();
    OnlineLogisticRegression lr = lmp.createRegression();
    
    for (int pass = 0; pass < passes; pass++) {
      BufferedReader in = open(inputFile);
      try {
        // read variable names, header
        csv.firstLine(in.readLine()) ;
        String line = null ;
        while ((line = in.readLine()) != null) {
          // for each new lineNum, get target and predictors
          Vector input = new RandomAccessSparseVector(lmp.getNumFeatures());
          int targetValue = csv.processLine(line, input);
          // check performance while this is still news
          double logP = lr.logLikelihood(targetValue, input);
          if (!Double.isInfinite(logP)) {
            if (samples < 20) {
              logPEstimate = (samples * logPEstimate + logP) / (samples + 1);
            } else {
              logPEstimate = 0.95 * logPEstimate + 0.05 * logP;
            }
            samples++;
          }
          double p = lr.classifyScalar(input);
          if (scores) {
            output.printf(Locale.ENGLISH,
                "%10d %2d %10.2f %2.4f %10.4f %10.4f\n", samples, targetValue,
                lr.currentLearningRate(), p, logP, logPEstimate);
          }

          // now update model
          lr.train(targetValue, input);
        }
      } finally {
        Closeables.closeQuietly(in);
      }
    }

    OutputStream modelOutput = new FileOutputStream(outputFile);
    try {
      lmp.saveTo(modelOutput);
    } finally {
      Closeables.closeQuietly(modelOutput);
    }

    output.printf(Locale.ENGLISH, "%d\n", lmp.getNumFeatures());
    output.printf(Locale.ENGLISH, "%s ~ ", lmp.getTargetVariable());
    String sep = "";
    for (String v : csv.getTraceDictionary().keySet()) {
      double weight = predictorWeight(lr, 0, csv, v);
      if (weight != 0) {
        output.printf(Locale.ENGLISH, "%s%.3f*%s", sep, weight, v);
        sep = " + ";
      }
    }
    output.printf("\n");
    model = lr;
    for (int row = 0; row < lr.getBeta().numRows(); row++) {
      for (String key : csv.getTraceDictionary().keySet()) {
        double weight = predictorWeight(lr, row, csv, key);
        if (weight != 0) {
          output.printf(Locale.ENGLISH, "%20s %.5f\n", key, weight);
        }
      }
      for (int column = 0; column < lr.getBeta().numCols(); column++) {
        output.printf(Locale.ENGLISH, "%15.9f ", lr.getBeta().get(row, column));
      }
      output.println();
    }
  }

  private static double predictorWeight(OnlineLogisticRegression lr, int row, RecordFactory csv, String predictor) {
    double weight = 0;
    for (Integer column : csv.getTraceDictionary().get(predictor)) {
      weight += lr.getBeta().get(row, column);
    }
    return weight;
  }

  static BufferedReader open(String inputFile) throws IOException {
    InputStream in;
    try {
      in = Resources.getResource(inputFile).openStream();
    } catch (IllegalArgumentException e) {
      in = new FileInputStream(new File(inputFile));
    }
    return new BufferedReader(new InputStreamReader(in, Charsets.UTF_8));
  }
  
  static public void main(String[] args) throws Exception {
    new TrainLogistic().run(args) ;
  }
}