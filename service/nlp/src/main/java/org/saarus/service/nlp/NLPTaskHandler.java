package org.saarus.service.nlp;

import java.util.List;

import org.saarus.nlp.classify.liblinear.TextClassifyDataReader;
import org.saarus.nlp.classify.liblinear.TextTrainer;
import org.saarus.service.hadoop.HadoopInfo;
import org.saarus.service.sql.HiveTableDataReader;
import org.saarus.service.sql.SQLService;
import org.saarus.service.task.CallableTaskUnit;
import org.saarus.service.task.Parameters;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.task.TaskUnitHandler;
import org.saarus.service.task.TaskUnitResult;

public class NLPTaskHandler implements TaskUnitHandler {
  static public String NAME = "NLP" ;
  
  private SQLService sqlService ;
  
  public NLPTaskHandler() {
  }

  public NLPTaskHandler(SQLService sqlService) throws Exception {
    this.sqlService = sqlService ;
  }
  
  public String getName() { return NAME ; }
  
  public SQLService getSqlService() { return this.sqlService ; }
  public void setSqlService(SQLService service) { this.sqlService = service ; }

  
  public CallableTaskUnit<?> getCallableTaskUnit(TaskUnit taskUnit) {
    String name = taskUnit.getName();
    if("liblinearTrainText".equals(name))   return liblinearTrainText(taskUnit) ;
    if("liblinearPredictText".equals(name)) return liblinearPredictText(taskUnit) ;
    if("registerNLPFunctions".equals(name)) return registerNLPFunctions(taskUnit) ;
    return null ;
  }

  private CallableTaskUnit<String> liblinearTrainText(TaskUnit tunit) {
    CallableTaskUnit<String> callableUnit = new CallableTaskUnit<String>(tunit, new TaskUnitResult<String>()) {
      public String doCall() throws Exception {
        Parameters params = taskUnit.getParameters() ;
        String table = params.getString("table") ;
        String textField = params.getString("textField") ;
        String labelField = params.getString("labelField") ;
        String modelOutputLoc = params.getString("modelOutputLoc") ;
        String tmpDir = params.getString("tmpDir", "target/nlpTEMP") ;
        final HiveTableDataReader tableReader = 
          new HiveTableDataReader(sqlService, table, new String[] {textField, labelField}) ;
        tableReader.reset() ;
        TextClassifyDataReader dataReader = new TextClassifyDataReader()  {
          public Record next() throws Exception {
            List<String> cells = tableReader.nextRow() ;
            if(cells == null) return null ;
            return new Record(cells.get(0), cells.get(1));
          }

          public void close() throws Exception { tableReader.close() ; }
        };
        
        TextTrainer trainer = new TextTrainer() ;
        trainer.train(dataReader, tmpDir) ;
        dataReader.close() ;
        if(modelOutputLoc != null) {
          
        }
        return  "done" ;
      }
    };
    return callableUnit ;
  }
  
  private CallableTaskUnit<Boolean> liblinearPredictText(TaskUnit tunit) {
    CallableTaskUnit<Boolean> callableUnit = new CallableTaskUnit<Boolean>(tunit, new TaskUnitResult<Boolean>()) {
      public Boolean doCall() throws Exception {
        Parameters params = taskUnit.getParameters() ;
        
        return true ;
      }
    };
    return callableUnit ;
  }
  
  private CallableTaskUnit<Boolean> registerNLPFunctions(TaskUnit tunit) {
    CallableTaskUnit<Boolean> callableUnit = new CallableTaskUnit<Boolean>(tunit, new TaskUnitResult<Boolean>()) {
      public Boolean doCall() throws Exception {
        Parameters params = taskUnit.getParameters() ;
        sqlService.addJar(
          HadoopInfo.MASTER_NODE_SAARUS_LIB_LOC + "/saarus.service.nlp-1.0.jar"
        ) ;
          
        sqlService.executeSQL(
          "CREATE TEMPORARY FUNCTION nlp_classify AS 'org.saarus.service.nlp.hive.udf.UDFLiblinearTextClassify'"
        ) ;
        return true ;
      }
    };
    return callableUnit ;
  }
  
  public String toString() { return getName() ; }
}