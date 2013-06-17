package org.saarus.mahout.classifier.sgd;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.hadoop.mapred.RunningJob;
import org.saarus.service.hive.HiveService;
import org.saarus.service.task.CallableTaskUnit;
import org.saarus.service.task.Parameters;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.task.TaskUnitHandler;
import org.saarus.service.task.TaskUnitResult;

public class LogisticRegressionTaskHandler implements TaskUnitHandler {
  static public String NAME = "LogisticRegression" ;
  
  private HiveService hservice ;
  
  public LogisticRegressionTaskHandler() {
  }

  public LogisticRegressionTaskHandler(HiveService hservice) throws Exception {
    this.hservice = hservice ;
  }
  
  public String getName() { return NAME ; }
  
  public HiveService getHiveService() { return this.hservice ; }
  public void setHiveService(HiveService hservice) { this.hservice = hservice ; }

  
  public CallableTaskUnit<?> getCallableTaskUnit(TaskUnit taskUnit) {
    String name = taskUnit.getName();
    if("train".equals(name))   return train(taskUnit) ;
    if("predict".equals(name)) return predict(taskUnit) ;
    return null ;
  }

  private CallableTaskUnit<String> train(TaskUnit tunit) {
    CallableTaskUnit<String> callableUnit = new CallableTaskUnit<String>(tunit, new TaskUnitResult<String>()) {
      public String doCall() throws Exception {
        Parameters params = taskUnit.getParameters() ;
        String[] args = new String[]{
            "--input", params.getString("input"),
            "--output", params.getString("output"),
            "--target", params.getString("target"), 
            "--categories", params.getString("categories"),
            "--predictors", params.getString("predictors"),
            "--features", params.getString("features", "20"),
            "--passes", params.getString("passes", "100"),
            "--rate", params.getString("rate", "50")
        };
        TrainLogistic tl = new TrainLogistic().setHiveService(hservice) ;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        tl.train(args, pw) ;
        return  sw.toString() ;
      }
    };
    return callableUnit ;
  }
  
  private CallableTaskUnit<Boolean> predict(TaskUnit tunit) {
    CallableTaskUnit<Boolean> callableUnit = new CallableTaskUnit<Boolean>(tunit, new TaskUnitResult<Boolean>()) {
      public Boolean doCall() throws Exception {
        Parameters params = taskUnit.getParameters() ;
        MRLogisticRegressionDecoder decoder = new MRLogisticRegressionDecoder() ;
        String[] headers = params.getString("col-header").split(",") ;
        for(int i = 0; i < headers.length; i++) headers[i] = headers[i].trim() ;
        decoder.
          setInputUri(params.getString("input")).
          setOutputUri(params.getString("output")).
          setModelUri(params.getString("model")).
          setColumnHeaders(headers).
          setClusterMode("true".equals(params.get("clusterMode"))) ;
        RunningJob runningJob = decoder.run() ;
        return runningJob.isSuccessful() ;
      }
    };
    return callableUnit ;
  }
  
  public String toString() { return getName() ; }
}