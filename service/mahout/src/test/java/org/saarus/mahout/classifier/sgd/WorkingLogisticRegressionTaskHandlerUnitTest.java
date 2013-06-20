package org.saarus.mahout.classifier.sgd;

import org.junit.Test;
import org.saarus.service.hive.HiveService;
import org.saarus.service.task.Parameters;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.task.TaskUnitResult;

public class WorkingLogisticRegressionTaskHandlerUnitTest {
  static String HIVE2_SERVER_URL = "jdbc:hive2://hadoop1.saarus.org:10000" ;
  @Test
  public void testYelp() throws Exception {
    //"src/test/resources/review-training.csv" , hive://features
    String TRAIN_FILE = "sample/train.csv" ; 
    String MODEL_FILE = "target/yelp-features.model" ;
    
    HiveService hservice  = new HiveService(HIVE2_SERVER_URL, "hive", "");
    LogisticRegressionTaskHandler handler  = new LogisticRegressionTaskHandler(hservice);
    
    TaskUnit tunit = new TaskUnit() ;
    tunit.setName("train") ;
    Parameters params = tunit.getParameters() ;
    params.setString("input", TRAIN_FILE) ;
    params.setString("output", MODEL_FILE) ;
    params.setString("target", "vote_useful") ;
    params.setString("categories", "2") ;
    params.setString("predictors", "n:stars|n:review_count|n:vote_useful") ;
    params.setString("features", "20") ;
    params.setString("passes", "100") ;
    params.setString("rate", "50") ;
    TaskUnitResult<String> tresult = (TaskUnitResult<String>) handler.getCallableTaskUnit(tunit).call() ;
    System.out.println(tresult.getResult());
    
    System.out.println("-------------------------------------------------------------------------");
    
    tunit = new TaskUnit() ;
    tunit.setName("predict") ;
    params = tunit.getParameters() ;
    params.setString("input", "sample/test.csv") ;
    params.setString("output", "target/review-out") ;
    params.setString("model", MODEL_FILE) ;
//    params.setString("col-header", "stars, text, vote_funny, vote_useful, vote_cool,"+
//                                   "business_id, business_city, business_state, business_open, business_review_count, " +
//                                   "business_stars, user_review_count, user_average_stars,user_vote_funny,user_vote_useful,user_vote_cool") ;

    params.setString("col-header", "stars, review_count,vote_useful");

    params.setString("cluster-mode", "false") ;
    TaskUnitResult<Boolean>  predictResult = (TaskUnitResult<Boolean>) handler.getCallableTaskUnit(tunit).call() ;
    System.out.println(predictResult.getResult());
  }
}
