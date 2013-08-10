package org.saarus.mahout.classifier.sgd;

import org.junit.Test;
import org.saarus.service.sql.SQLService;
import org.saarus.service.task.Parameters;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.task.TaskUnitResult;

public class LogisticRegressionTaskHandlerUnitTest {
  static String HIVE2_SERVER_URL = "jdbc:hive2://hadoop1.saarus.org:10000" ;
  @Test
  public void test() throws Exception {
    //testDonut() ;
    testYelp() ;
  }
  
  void testDonut() throws Exception {
    String TRAIN_FILE = "src/test/resources/donut/donut.csv" ; //"src/test/resources/donut.csv" 
    String MODEL_FILE = "dfs:/tmp/donut.model" ;
    
    SQLService hservice  = new SQLService(HIVE2_SERVER_URL, "hive", "");
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
    TaskUnitResult<Boolean> predictResult = 
        (TaskUnitResult<Boolean>) handler.getCallableTaskUnit(tunit).call() ;
    System.out.println(predictResult.getResult());
  }
  
  void testYelp() throws Exception {
    //"src/test/resources/review-training.csv" , hive://features
    String TRAIN_FILE = "src/test/resources/yelp/train.csv" ; 
    String MODEL_FILE = "target/yelp-features.model" ;
    
    SQLService hservice  = null ; //new SQLService(HIVE2_SERVER_URL, "hive", "");
    LogisticRegressionTaskHandler handler  = new LogisticRegressionTaskHandler(hservice);
    
    TaskUnit tunit = new TaskUnit() ;
    tunit.setName("train") ;
    LogisticRegressionTrainerConfig config = new LogisticRegressionTrainerConfig() ;
    config.setInput(TRAIN_FILE) ;
    config.setOutput(MODEL_FILE) ;
    config.setTarget("cat_useful") ;
    config.setCategories("2") ;
    config.addPredictor("user_review_count", "n").
           addPredictor("user_average_stars", "n").
           addPredictor("user_vote_useful", "n").
           addPredictor("stars", "n").
           addPredictor("business_stars", "n").
           addPredictor("business_review_count", "n").
           addPredictor("vote_useful", "n").
           addPredictor("vote_funny", "n").
           addPredictor("vote_cool", "n").
           addPredictor("percentage_useful", "n");
    config.setFeatures("1000");
    config.setPasses("100") ;
    config.setRate("50") ;
    config.setLambda("0.001") ;
    config.setMaxRead(20000) ;
    tunit.setTaskUnitConfig(config) ;

    TaskUnitResult<String> tresult = (TaskUnitResult<String>) handler.getCallableTaskUnit(tunit).call() ;
    System.out.println(tresult.getResult());
    
    System.out.println("-------------------------------------------------------------------------");
    
    tunit = new TaskUnit() ;
    tunit.setName("predict") ;
    LogisticRegressionPredictorConfig predictConfig = new LogisticRegressionPredictorConfig() ;
    predictConfig.setInput("src/test/resources/yelp/test-no-header.csv") ;
    predictConfig.setOutput("target/review-out") ;
    predictConfig.setModelLocation(MODEL_FILE) ;
    predictConfig.
      addFieldName("stars").
      addFieldName("text").
      addFieldName("vote_funny").
      addFieldName("vote_useful").
      addFieldName("vote_cool").
      addFieldName("percentage_useful").
      addFieldName("cat_useful").
      addFieldName("business_id").
      addFieldName("business_city").
      addFieldName("business_state").
      addFieldName("business_open").
      addFieldName("business_review_count").
      addFieldName("business_stars").
      addFieldName("user_review_count").
      addFieldName("user_average_stars").
      addFieldName("user_vote_funny").
      addFieldName("user_vote_useful").
      addFieldName("user_vote_cool") ;
    predictConfig.setClusterMode(false) ;
    tunit.setTaskUnitConfig(predictConfig) ;

    TaskUnitResult<Boolean>  predictResult = (TaskUnitResult<Boolean>) handler.getCallableTaskUnit(tunit).call() ;
    System.out.println(predictResult.getResult());
  }
}
