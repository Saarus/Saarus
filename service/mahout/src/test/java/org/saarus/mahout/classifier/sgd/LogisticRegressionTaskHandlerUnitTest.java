package org.saarus.mahout.classifier.sgd;

import org.junit.Test;
import org.saarus.service.hive.HiveService;
import org.saarus.service.task.TaskUnit;

public class LogisticRegressionTaskHandlerUnitTest {
  @Test
  public void test() throws Exception {
    HiveService hservice  = new HiveService("jdbc:hive2://198.154.60.252:10000", "hive", "");
    LogisticRegressionTaskHandler handler  = new LogisticRegressionTaskHandler(hservice);
    TaskUnit tunit = new TaskUnit() ;
    tunit.setName("train") ;
    tunit.getParameters().setString("table", "donut_train") ;
    tunit.getParameters().setString("features", "") ;
  }
}
