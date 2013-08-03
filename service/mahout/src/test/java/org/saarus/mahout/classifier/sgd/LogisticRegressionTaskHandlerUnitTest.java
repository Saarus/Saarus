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
    Parameters params = tunit.getParameters() ;
    params.setString("input", TRAIN_FILE) ;
    params.setString("output", MODEL_FILE) ;
    params.setString("target", "cat_useful") ;
    params.setString("categories", "2") ;
    
    params.setString("predictors", "n:user_review_count | n:user_average_stars | n:user_vote_useful | n:stars | n:business_stars | n:business_review_count | n:vote_useful | n:vote_funny | n:vote_cool | n:percentage_useful") ;
    //params.setString("predictors", "n:user_review_count|n:user_average_stars|n:user_vote_useful|n:stars|n:business_stars|n:business_review_count") ;
    params.setString("features", "1000") ;
    params.setString("passes", "100") ;
    params.setString("rate", "50") ;
    TaskUnitResult<String> tresult = (TaskUnitResult<String>) handler.getCallableTaskUnit(tunit).call() ;
    System.out.println(tresult.getResult());
    
    System.out.println("-------------------------------------------------------------------------");
    
    tunit = new TaskUnit() ;
    tunit.setName("predict") ;
    params = tunit.getParameters() ;
    params.setString("input", "src/test/resources/yelp/test-no-header.csv") ;
    params.setString("output", "target/review-out") ;
    params.setString("model", MODEL_FILE) ;
//    params.setString("col-header", "stars, text, vote_funny, vote_useful, vote_cool,"+
//                                   "business_id, business_city, business_state, business_open, business_review_count, " +
//                                   "business_stars, user_review_count, user_average_stars,user_vote_funny,user_vote_useful,user_vote_cool") ;

    params.setString("col-header", "stars,text,vote_funny,vote_useful,vote_cool,percentage_useful,cat_useful,business_id,business_city,business_state,business_open,business_review_count,business_stars,user_review_count,user_average_stars,user_vote_funny,user_vote_useful,user_vote_cool");

    params.setString("cluster-mode", "false") ;
    TaskUnitResult<Boolean>  predictResult = (TaskUnitResult<Boolean>) handler.getCallableTaskUnit(tunit).call() ;
    System.out.println(predictResult.getResult());
  }
}
