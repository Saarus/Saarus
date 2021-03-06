package org.saarus.mahout.classifier.sgd;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.classifier.sgd.RecordFactory;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.saarus.service.hadoop.util.FSResource;
import org.saarus.service.sql.DataReader;
import org.saarus.service.sql.SQLService;

import com.google.common.base.Charsets;
import com.google.common.io.Closeables;
import com.google.common.io.Resources;
/**
 * Train a logistic regression for the examples from Chapter 13 of Mahout in Action
 */
public final class TrainLogistic {

  private SQLService hservice ;
  private LogisticModelParameters lmp ;
  private int maxRead = 1000000000 ;
  
  public TrainLogistic setHiveService(SQLService hservice) {
    this.hservice = hservice ;
    return this ;
  }
  
  public LogisticModelParameters getParameters() { return this.lmp ; }
  
  public  OnlineLogisticRegression getModel() { return this.lmp.createRegression() ; }
  
  public void setMaxRead(int num) {
    this.maxRead = num ;
  }
  
  void train(String[] args, PrintWriter output) throws Exception {
    TrainLogisticArgumentParser argParser = 
        new TrainLogisticArgumentParser().setHiveService(hservice) ;
    argParser.parse(args) ;
    
    double logPEstimate = 0;
    int samples = 0;
    
    this.lmp = argParser.getLogisticModelParameters();
    RecordFactoryImpl csv = lmp.getCsvRecordFactory();
    OnlineLogisticRegression lr = lmp.createRegression();
    DataReader dataReader = argParser.getDataReader() ;
    csv.configVariableNames(dataReader.getHeaderNames());
    System.out.println("Max Read = " + maxRead);
    for (int pass = 0; pass < argParser.passes; pass++) {
      dataReader.reset() ;
      try {
        // read variable names
        List<String> rowData = null ;
        int count = 0 ;
        while (count < maxRead && (rowData = dataReader.nextRow()) != null) {
          count++ ;
          // for each new lineNum, get target and predictors
          Vector input = new RandomAccessSparseVector(lmp.getNumFeatures());
          int targetValue = csv.processData(rowData, input);

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
          if (argParser.scores) {
            output.printf(Locale.ENGLISH, "%10d %2d %10.2f %2.4f %10.4f %10.4f\n",
                samples, targetValue, lr.currentLearningRate(), p, logP, logPEstimate);
          }

          // now update model
          lr.train(targetValue, input);
        }
      } finally {
        dataReader.close();
      }
    }

    ByteArrayOutputStream modelOutput = new ByteArrayOutputStream();
    try {
      lmp.saveTo(modelOutput);
    } finally {
      Closeables.closeQuietly(modelOutput);
    }
    FSResource modelFSResource = argParser.getModelFSResource() ; 
    modelFSResource.write(modelOutput.toByteArray()) ;
    
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
  
  public static void main(String[] args) throws Exception {
    TrainLogistic tl = new TrainLogistic() ;
    tl.train(args, new PrintWriter(System.out, true));
  }
}
