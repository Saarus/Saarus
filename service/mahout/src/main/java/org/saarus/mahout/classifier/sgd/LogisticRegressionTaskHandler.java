package org.saarus.mahout.classifier.sgd;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.hadoop.mapred.RunningJob;
import org.saarus.service.sql.SQLService;
import org.saarus.service.task.CallableTaskUnit;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.task.TaskUnitHandler;
import org.saarus.service.task.TaskUnitResult;

public class LogisticRegressionTaskHandler implements TaskUnitHandler {
  static public String NAME = "LogisticRegression" ;
  
  private SQLService sqlService ;
  
  public LogisticRegressionTaskHandler() {
  }

  public LogisticRegressionTaskHandler(SQLService sqlService) throws Exception {
    this.sqlService = sqlService ;
  }
  
  public String getName() { return NAME ; }
  
  public SQLService getSqlService() { return this.sqlService ; }
  public void setSqlService(SQLService service) { this.sqlService = service ; }

  
  public CallableTaskUnit<?> getCallableTaskUnit(TaskUnit taskUnit) {
    String name = taskUnit.getName();
    if("train".equals(name))   return train(taskUnit) ;
    if("predict".equals(name)) return predict(taskUnit) ;
    return null ;
  }

  private CallableTaskUnit<String> train(TaskUnit tunit) {
    CallableTaskUnit<String> callableUnit = new CallableTaskUnit<String>(tunit, new TaskUnitResult<String>()) {
      public String doCall() throws Exception {
        LogisticRegressionTrainerConfig config = (LogisticRegressionTrainerConfig) taskUnit.getTaskUnitConfig() ;
        String[] args = new String[] {
            "--input", config.getInput(),
            "--output", config.getOutput(),
            "--target", config.getTarget(), 
            "--categories", config.getCategories() ,
            "--predictors", config.getPredictorParameters(),
            "--features", config.getFeatures(),
            "--passes", config.getPasses(),
            "--rate", config.getRate(),
            "--lambda", config.getLambda()
        };
        TrainLogistic tl = new TrainLogistic().setHiveService(sqlService) ;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        if(config.getMaxRead() > 0) {
          tl.setMaxRead(config.getMaxRead()) ;
        }
        tl.train(args, pw) ;
        return  sw.toString() ;
      }
    };
    return callableUnit ;
  }
  
  private CallableTaskUnit<Boolean> predict(TaskUnit tunit) {
    CallableTaskUnit<Boolean> callableUnit = new CallableTaskUnit<Boolean>(tunit, new TaskUnitResult<Boolean>()) {
      public Boolean doCall() throws Exception {
        LogisticRegressionPredictorConfig predictConfig = 
          (LogisticRegressionPredictorConfig) taskUnit.getTaskUnitConfig() ;
        
        MRLogisticRegressionDecoder decoder = new MRLogisticRegressionDecoder() ;
        decoder.
          setInputUri(predictConfig.getInput()).
          setOutputUri(predictConfig.getOutput()).
          setModelUri(predictConfig.getModelLocation()).
          setColumnHeaders(predictConfig.getFieldNameArray()).
          setClusterMode(predictConfig.isClusterMode()) ;
        RunningJob runningJob = decoder.run() ;
        return runningJob.isSuccessful() ;
      }
    };
    return callableUnit ;
  }
  
  public String toString() { return getName() ; }
}