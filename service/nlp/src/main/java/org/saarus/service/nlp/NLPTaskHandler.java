package org.saarus.service.nlp;

import java.io.File;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.util.List;

import org.saarus.nlp.classify.liblinear.TextClassifyDataReader;
import org.saarus.nlp.classify.liblinear.TextTrainer;
import org.saarus.service.hadoop.HadoopInfo;
import org.saarus.service.hadoop.util.FSResource;
import org.saarus.service.sql.HiveTableDataReader;
import org.saarus.service.sql.QueryResult;
import org.saarus.service.sql.SQLService;
import org.saarus.service.task.CallableTaskUnit;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.task.TaskUnitHandler;
import org.saarus.service.task.TaskUnitResult;

public class NLPTaskHandler implements TaskUnitHandler {
  static public String NAME = "NLP" ;
  
  private SQLService sqlService ;
  
  public NLPTaskHandler() {
  }

  public NLPTaskHandler(SQLService sqlService) throws Exception {
    setSqlService(sqlService) ;
  }
  
  public String getName() { return NAME ; }
  
  public SQLService getSqlService() { return this.sqlService ; }
  public void setSqlService(SQLService service) throws Exception { 
    this.sqlService = service ; 
    sqlService.addJar(
        HadoopInfo.MASTER_NODE_SAARUS_LIB_LOC + "/trove4j-3.0.3.jar",
        HadoopInfo.MASTER_NODE_SAARUS_LIB_LOC + "/stanford-corenlp-1.3.5.jar",
        HadoopInfo.MASTER_NODE_SAARUS_LIB_LOC + "/lucene-snowball-3.0.3.jar",
        HadoopInfo.MASTER_NODE_SAARUS_LIB_LOC + "/liblinear-1.92.jar",
        HadoopInfo.MASTER_NODE_SAARUS_LIB_LOC + "/saarus.lib.common-1.0.jar",
        HadoopInfo.MASTER_NODE_SAARUS_LIB_LOC + "/saarus.lib.nlp.core-1.0.jar",
        HadoopInfo.MASTER_NODE_SAARUS_LIB_LOC + "/saarus.lib.nlp.classify-1.0.jar",
        HadoopInfo.MASTER_NODE_SAARUS_LIB_LOC + "/saarus.service.hadoop-1.0.jar",
        HadoopInfo.MASTER_NODE_SAARUS_LIB_LOC + "/saarus.service.nlp-1.0.jar"
      ) ;
        
      sqlService.executeSQL(
        "CREATE TEMPORARY FUNCTION nlp_classify AS 'org.saarus.service.nlp.hive.udf.UDFLiblinearTextClassify'"
      ) ;
  }

  
  public CallableTaskUnit<?> getCallableTaskUnit(TaskUnit taskUnit) {
    String name = taskUnit.getName();
    if("liblinearTrainText".equals(name))   return liblinearTrainText(taskUnit) ;
    if("liblinearPredictText".equals(name)) return liblinearPredictText(taskUnit) ;
    return null ;
  }

  private CallableTaskUnit<String> liblinearTrainText(TaskUnit tunit) {
    CallableTaskUnit<String> callableUnit = new CallableTaskUnit<String>(tunit, new TaskUnitResult<String>()) {
      public String doCall() throws Exception {
        NLPLiblinearTrainTextConfig config = (NLPLiblinearTrainTextConfig) taskUnit.getTaskUnitConfig() ;
        final HiveTableDataReader tableReader = 
          new HiveTableDataReader(sqlService, config.getTable(), new String[] {config.getLabelField(), config.getTextField()}) ;
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
        trainer.train(dataReader, config.getTmpDir()) ;
        dataReader.close() ;
        if(config.getModelOutputLoc() != null) {
          File[] files = new File(config.getTmpDir()).listFiles() ;
          for(File sel : files) {
            FSResource res = FSResource.get(config.getModelOutputLoc() + "/" + sel.getName()) ;
            FileInputStream is = new FileInputStream(sel) ;
            int bytes = res.write(is) ;
            System.out.println(sel.getName() + " , write " + bytes + " bytes");
            is.close() ;
          }
        }
        return  "done" ;
      }
    };
    return callableUnit ;
  }
  
  private CallableTaskUnit<QueryResult> liblinearPredictText(TaskUnit tunit) {
    CallableTaskUnit<QueryResult> callableUnit = new CallableTaskUnit<QueryResult>(tunit, new TaskUnitResult<QueryResult>()) {
      public QueryResult doCall() throws Exception {
        ResultSet res = sqlService.executeQuerySQL(taskUnit.getTaskLine());
        QueryResult qresult = new QueryResult(taskUnit.getTaskLine(), res) ;
        return  qresult ;
      }
    };
    return callableUnit ;
  }
  
  public String toString() { return getName() ; }
}