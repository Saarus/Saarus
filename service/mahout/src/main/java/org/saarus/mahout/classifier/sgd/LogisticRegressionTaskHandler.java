package org.saarus.mahout.classifier.sgd;

import java.sql.ResultSet;

import org.saarus.service.hive.HiveService;
import org.saarus.service.hive.QueryResult;
import org.saarus.service.task.CallableTaskUnit;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.task.TaskUnitHandler;
import org.saarus.service.task.TaskUnitResult;

public class LogisticRegressionTaskHandler implements TaskUnitHandler {
  private HiveService hservice ;
  
  public LogisticRegressionTaskHandler() {
  }

  public LogisticRegressionTaskHandler(HiveService hservice) throws Exception {
    this.hservice = hservice ;
  }
  
  public String getName() { return "LogisticRegression" ; }
  
  public CallableTaskUnit<?> getCallableTaskUnit(TaskUnit taskUnit) {
    String name = taskUnit.getName();
    if("train".equals(name))   return train(taskUnit) ;
    return null ;
  }

  private CallableTaskUnit<QueryResult> train(TaskUnit tunit) {
    CallableTaskUnit<QueryResult> callableUnit = new CallableTaskUnit<QueryResult>(tunit, new TaskUnitResult<QueryResult>()) {
      public QueryResult doCall() throws Exception {
        String table = taskUnit.getParameters().getString("table", null) ;
        String features = taskUnit.getParameters().getString("features", null) ;
        ResultSet res = hservice.executeQuerySQL(taskUnit.getTaskLine());
        while(res.next()) {
          res.getObject("name") ;
        }
        res.close() ;
        QueryResult qresult = new QueryResult() ;
        return  qresult ;
      }
    };
    return callableUnit ;
  }
}