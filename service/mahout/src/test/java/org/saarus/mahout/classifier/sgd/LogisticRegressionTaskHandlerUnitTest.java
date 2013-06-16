package org.saarus.mahout.classifier.sgd;

import org.junit.Test;
import org.saarus.service.hive.HiveService;
import org.saarus.service.task.Parameters;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.task.TaskUnitResult;

public class LogisticRegressionTaskHandlerUnitTest {
  @Test
  public void test() throws Exception {
    //testDonut() ;
    testYelp() ;
  }
  
  void testDonut() throws Exception {
    String TRAIN_FILE = "hive://donut_train" ; //"src/test/resources/donut.csv" 
    String MODEL_FILE = "dfs:/tmp/donut.model" ;
    
    HiveService hservice  = new HiveService("jdbc:hive2://198.154.60.252:10000", "hive", "");
    LogisticRegressionTaskHandler handler  = new LogisticRegressionTaskHandler(hservice);
    
    TaskUnit tunit = new TaskUnit() ;
    tunit.setName("train") ;
    Parameters params = tunit.getParameters() ;
    params.setString("input", TRAIN_FILE) ;
    params.setString("output", MODEL_FILE) ;
    params.setString("target", "color") ;
    params.setString("categories", "2") ;
    params.setString("predictors", "n:x | n:y | n:a | n:b | n:c") ;
    params.setString("features", "20") ;
    params.setString("passes", "100") ;
    params.setString("rate", "50") ;
    TaskUnitResult<String> tresult = 
        (TaskUnitResult<String>) handler.getCallableTaskUnit(tunit).call() ;
    System.out.println(tresult.getResult());
    
    System.out.println("-------------------------------------------------------------------------");
    
    tunit = new TaskUnit() ;
    tunit.setName("predict") ;
    params = tunit.getParameters() ;
    params.setString("input", "src/test/resources/donutmr") ;
    params.setString("output", "target/output") ;
    params.setString("model", MODEL_FILE) ;
    params.setString("col-header", "x,y,shape,color,xx,xy,yy,c,a,b") ;
    params.setString("cluster-mode", "false") ;
    tresult = (TaskUnitResult<String>) handler.getCallableTaskUnit(tunit).call() ;
    System.out.println(tresult.getResult());
  }
  
  void testYelp() throws Exception {
    String TRAIN_FILE = "hive://features" ; //"src/test/resources/review-training" , hive://features
    String MODEL_FILE = "dfs:/tmp/yelp-features.model" ;
    
    HiveService hservice  = new HiveService("jdbc:hive2://198.154.60.252:10000", "hive", "");
    LogisticRegressionTaskHandler handler  = new LogisticRegressionTaskHandler(hservice);
    
    TaskUnit tunit = new TaskUnit() ;
    tunit.setName("train") ;
    Parameters params = tunit.getParameters() ;
    params.setString("input", TRAIN_FILE) ;
    params.setString("output", MODEL_FILE) ;
    params.setString("target", "vote_useful") ;
    params.setString("categories", "2") ;
    params.setString("predictors", "n:stars | n:business_review_count") ;
    params.setString("features", "20") ;
    params.setString("passes", "50") ;
    params.setString("rate", "50") ;
//    TaskUnitResult<String> tresult = 
//        (TaskUnitResult<String>) handler.getCallableTaskUnit(tunit).call() ;
//    System.out.println(tresult.getResult());
    
    System.out.println("-------------------------------------------------------------------------");
    
    tunit = new TaskUnit() ;
    tunit.setName("predict") ;
    params = tunit.getParameters() ;
    params.setString("input", "src/test/resources/reviewmr") ;
    params.setString("output", "target/review-out") ;
    params.setString("model", MODEL_FILE) ;
    params.setString("col-header", "stars, text, vote_funny, vote_useful, vote_cool,"+
                                   "business_id, business_city, business_state, business_open, business_review_count, " +
                                   "business_stars, user_review_count, user_average_stars") ;
    params.setString("cluster-mode", "false") ;
    TaskUnitResult<String>  predictResult = (TaskUnitResult<String>) handler.getCallableTaskUnit(tunit).call() ;
    System.out.println(predictResult.getResult());
  }
}
