package org.saarus.mahout.classifier.sgd;

import org.junit.Test;
import org.saarus.service.hive.HiveService;
import org.saarus.service.task.Parameters;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.task.TaskUnitResult;

public class LogisticRegressionTaskHandlerUnitTest {
  @Test
  public void test() throws Exception {
    String TRAIN_FILE = "hive://donut_train" ; //"src/test/resources/donut.csv" 
    String MODEL_FILE = "dfs:/tmp/donut.model" ;
    
    HiveService hservice  = new HiveService("jdbc:hive2://198.154.60.252:10000", "hive", "");
    LogisticRegressionTaskHandler handler  = new LogisticRegressionTaskHandler(hservice);
    
    TaskUnit tunit = new TaskUnit() ;
    tunit.setName("train") ;
    Parameters params = tunit.getParameters() ;
    params.setString("--input", TRAIN_FILE) ;
    params.setString("--output", MODEL_FILE) ;
    params.setString("--target", "color") ;
    params.setString("--categories", "2") ;
    params.setString("--predictors", "n:x | n:y | n:a | n:b | n:c") ;
    params.setString("--features", "20") ;
    params.setString("--passes", "100") ;
    params.setString("--rate", "50") ;
    TaskUnitResult<String> tresult = 
        (TaskUnitResult<String>) handler.getCallableTaskUnit(tunit).call() ;
    System.out.println(tresult.getResult());
    
    System.out.println("-------------------------------------------------------------------------");
    
    tunit = new TaskUnit() ;
    tunit.setName("predict") ;
    params = tunit.getParameters() ;
    params.setString("--input", "src/test/resources/donut/donut-test.csv") ;
    params.setString("--model", MODEL_FILE) ;
    tresult = (TaskUnitResult<String>) handler.getCallableTaskUnit(tunit).call() ;
    System.out.println(tresult.getResult());
  }
}
