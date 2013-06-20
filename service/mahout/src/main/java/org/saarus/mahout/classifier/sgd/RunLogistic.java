package org.saarus.mahout.classifier.sgd;

import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import org.apache.mahout.classifier.evaluation.Auc;
import org.apache.mahout.classifier.sgd.OnlineLogisticRegression;
import org.apache.mahout.math.Matrix;
import org.apache.mahout.math.SequentialAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.saarus.service.hadoop.util.FSResource;

public final class RunLogistic {
  public void predict(String[] args, PrintWriter output) throws Exception {
    RunLogisticArgumentParser argParser = new RunLogisticArgumentParser() ;
    argParser.parseArgs(args) ;
    boolean showAuc = argParser.getShowAuc() ;
    boolean showConfusion = argParser.getShowConfusion() ;
    boolean showScores = argParser.getShowScores() ;
    if (!showAuc && !showConfusion && !showScores) {
      showAuc = true;
      showConfusion = true;
    }

    Auc collector = new Auc();
    FSResource modelFSResource = argParser.getModelFSResource() ;
    LogisticModelParameters lmp = LogisticModelParameters.loadFrom(modelFSResource.getInputStream());

    RecordFactoryImpl csv = lmp.getCsvRecordFactory();
    OnlineLogisticRegression lr = lmp.createRegression();
    DataReader dataReader = argParser.getDataReader() ;
    dataReader.reset() ;
    csv.configVariableNames(dataReader.getHeaderNames()) ;
    if (showScores) {
      output.printf(Locale.ENGLISH, "\"%s\", \"%s\", \"%s\", \"%s\"\n", "target", "target label",  "model-output", "log-likelihood");
    }
    List<String> rowData = null ;
    while((rowData = dataReader.nextRow()) != null) {
      Vector v = new SequentialAccessSparseVector(lmp.getNumFeatures());
      int target = csv.processData(rowData, v);
      String targetLabel = csv.getTargetLabel(target) ;
      double score = lr.classifyScalar(v);
      if (showScores) {
        output.printf(Locale.ENGLISH, "%d, %s, %.3f, %.6f\n", target, targetLabel, score, lr.logLikelihood(target, v));
      }
      collector.add(target, score);
    }

    if (showAuc) {
      output.printf(Locale.ENGLISH, "AUC = %.2f\n", collector.auc());
    }
    if (showConfusion) {
      Matrix m = collector.confusion();
      output.printf(Locale.ENGLISH, "confusion: [[%.1f, %.1f], [%.1f, %.1f]]\n", m.get(0, 0), m.get(1, 0), m.get(0, 1), m.get(1, 1));
      m = collector.entropy();
      output.printf(Locale.ENGLISH, "entropy: [[%.1f, %.1f], [%.1f, %.1f]]\n", m.get(0, 0), m.get(1, 0), m.get(0, 1), m.get(1, 1));
    }
  }

  public static void main(String[] args) throws Exception {
    new RunLogistic().predict(args, new PrintWriter(System.out, true));
  }
}
