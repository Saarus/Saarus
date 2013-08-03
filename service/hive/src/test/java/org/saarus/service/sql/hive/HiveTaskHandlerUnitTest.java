package org.saarus.service.sql.hive;

import java.util.List;

import org.junit.Test;
import org.saarus.service.hadoop.HadoopInfo;
import org.saarus.service.sql.QueryResult;
import org.saarus.service.sql.SQLService;
import org.saarus.service.sql.TableMetadata;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.task.TaskUnitResult;

public class HiveTaskHandlerUnitTest {
  @Test
  public void testClient() throws Exception {
    SQLService hservice  = new SQLService(HadoopInfo.HIVE_CONNECTION_URL, "anonymous", "");
    HiveTaskHandler handler  = new HiveTaskHandler(hservice);

    String tableName = "testtable";
    System.out.println(handler.dropTable(tableName)) ;
    
    handler.execute("CREATE EXTERNAL TABLE " + tableName + " (key INT, value STRING) STORED AS RCFILE LOCATION '/tmp/testdb/testtable'");
    // show tables
    TaskUnitResult<List<String>> listResult = handler.listTables() ;
    List<String> listTables = listResult.getResult();
    System.out.println(listResult);
    System.out.println("Available Tables: ");
    for(String selTable : listTables) {
      System.out.println("  " + selTable);
    }
    // describe table
    TaskUnitResult<TableMetadata> descResult = handler.describeTable(tableName) ;
    System.out.println(descResult);
    //describe tables
    long start = System.currentTimeMillis() ;
    String[] tables = listTables.toArray(new String[listTables.size()]) ;
    TaskUnitResult<TableMetadata[]> descTablesResult = handler.describeTables(tables) ;
    for(TableMetadata sel : descTablesResult.getResult()) {
      System.out.println("TableMetadata " + sel.getTableName());
    }
    System.out.println("Desc tables in " + (System.currentTimeMillis() - start) + "ms");
    
    
    doImportTask(handler, createImportJsonTask()) ;
    doImportTask(handler, createImportCsvTask()) ;
  }
  
  private void doImportTask(HiveTaskHandler handler, TaskUnit importTask) throws Exception {
    TaskUnitResult<String> importResult = 
        (TaskUnitResult<String>)handler.getCallableTaskUnit(importTask).call() ;
    System.out.println(importResult);
    
    // select * query
    TaskUnitResult<QueryResult> queryResult = handler.executeQuery("SELECT * FROM testtable");
    System.out.println(queryResult);
    queryResult.getResult().dump(20, 20) ;
  }
  
  private TaskUnit createImportJsonTask() {
    TaskUnit tunit = new TaskUnit() ;
    tunit.setName("importJson") ;
    tunit.getParameters().setString("dbfile", "/tmp/testdb/testtable/data-json.rcfile") ;
    tunit.getParameters().setString("file", "src/test/resources/testtable.json") ;
    tunit.getParameters().setStringArray("properties", new String[]{"key", "value"}) ;
    return tunit ;
  }
  
  private TaskUnit createImportCsvTask() {
    TaskUnit tunit = new TaskUnit() ;
    tunit.setName("importCsv") ;
    tunit.getParameters().setString("dbfile", "/tmp/testdb/testtable/data-csv.rcfile") ;
    tunit.getParameters().setString("file", "src/test/resources/testtable.csv") ;
    tunit.getParameters().setStringArray("properties", new String[]{"key", "value"}) ;
    return tunit ;
  }
}