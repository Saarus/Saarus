package org.saarus.service.nlp;

import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.saarus.service.hadoop.HadoopInfo;
import org.saarus.service.hadoop.util.HDFSUtil;
import org.saarus.service.sql.SQLService;
import org.saarus.service.sql.SQLTable;
import org.saarus.service.sql.TableMetadata;
import org.saarus.service.sql.io.TableRCFileWriter;
import org.saarus.service.task.Parameters;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.task.TaskUnitResult;

public class NLPTaskHandlerUnitTest {
  static final String TABLE_NAME = "twitter";
  static final String TABLE_LOCATION = "/tmp/testdb/twitter" ;
  static final String MODEL_LOCATION = "dfs:/tmp/twitter/model" ;
  
  @Test
  public void testNLPTaskHandlerFunctions() throws Exception {
    SQLService sqlService  = new SQLService(HadoopInfo.HIVE_CONNECTION_URL, "hive", "");
    NLPTaskHandler handler  = new NLPTaskHandler(sqlService);
    TaskUnitResult<Boolean> registerResult = 
      (TaskUnitResult<Boolean>)handler.getCallableTaskUnit(this.createRegisterNLPFunctions()).call() ;
    Assert.assertTrue(registerResult.getResult()) ;
    testTrain(sqlService, handler) ;
  }
  
  private void testTrain(SQLService sqlService, NLPTaskHandler handler) throws Exception {
    TableMetadata tmeta = createTwitterTableMetadata("twitter_train") ;
    SQLTable sqlTable = new SQLTable(sqlService, tmeta) ;
    sqlTable.dropTable() ;
    sqlTable.createTable(TABLE_LOCATION + "/" + tmeta.getTableName()) ;
    Configuration conf = HDFSUtil.getConfiguration() ;
    TableRCFileWriter writer = sqlTable.createTableWriter(conf, "data0.rcfile") ;
    writer.writeRow("1", "I like my iphone", "POSITIVE", "") ;
    writer.writeRow("2", "the iphone is very light and slim", "POSITIVE", "") ;
    writer.writeRow("3", "iphone battery is drained very fast", "NEGATIVE", "") ;
    writer.writeRow("4", "The iphone has a high resolution screen", "POSITIVE", "") ;
    writer.close() ;
    
    TaskUnit trainTaskUnit = new TaskUnit() ;
    trainTaskUnit.setName("liblinearTrainText") ;
    Parameters params = trainTaskUnit.getParameters() ;
    params.setString("table", tmeta.getTableName()) ;
    params.setString("textField", "tweet") ;
    params.setString("labelField", "label") ;
    params.setString("modelOutputLoc", MODEL_LOCATION) ;
    params.setString("tmpDir", "target/nlpTEMP") ;
    TaskUnitResult<String> trainResult = 
        (TaskUnitResult<String>) handler.getCallableTaskUnit(trainTaskUnit).call() ;
    System.out.println("Train Result = " + trainResult.getResult());
  }
  
  private void testPredict(SQLService sqlService, NLPTaskHandler handler) throws Exception {
    TableMetadata tmeta = createTwitterTableMetadata("twitter_predict") ;
    SQLTable sqlTable = new SQLTable(sqlService, tmeta) ;
    sqlTable.dropTable() ;
    sqlTable.createTable(TABLE_LOCATION + "/" + tmeta.getTableName()) ;
    Configuration conf = HDFSUtil.getConfiguration() ;
    TableRCFileWriter writer = sqlTable.createTableWriter(conf, "data0.rcfile") ;
    writer.close() ;
  }

  private TaskUnit createRegisterNLPFunctions() {
    TaskUnit tunit = new TaskUnit() ;
    tunit.setName("registerNLPFunctions") ;
    return tunit ;
  }
  
  private TableMetadata createTwitterTableMetadata(String tableName) throws Exception {
    TableMetadata tmeta = new TableMetadata(tableName) ;
    tmeta.addField("id", "INT") ;
    tmeta.addField("tweet", "STRING") ;
    tmeta.addField("label", "STRING") ;
    tmeta.addField("predict", "STRING") ;
    return tmeta ;
  }
}
