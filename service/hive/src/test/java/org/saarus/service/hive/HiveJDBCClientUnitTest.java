package org.saarus.service.hive;

import java.util.List;

import org.junit.Test;
import org.saarus.service.task.TaskUnitResult;

public class HiveJDBCClientUnitTest {
  @Test
  public void testClient() throws Exception {
    HiveService hservice  = new HiveService("jdbc:hive2://198.154.60.252:10000", "hive", "");
    HiveTaskHandler client  = new HiveTaskHandler(hservice);

    String tableName = "testtable";
    System.out.println(client.dropTable(tableName)) ;
    client.execute("CREATE EXTERNAL TABLE " + tableName + " (key INT, value STRING) location '/user/hive/testdb/testtable'");
    // show tables
    TaskUnitResult<List<String>> listResult = client.listTables() ;
    System.out.println(listResult);
    System.out.println("Available Tables: ");
    for(String selTable : listResult.getResult()) {
      System.out.println("  " + selTable);
    }
    // describe table
    TaskUnitResult<TableMetadata> descResult = client.describeTable(tableName) ;
    System.out.println(descResult);
    
    //boolean loadData = 
    //    client.execute("LOAD DATA LOCAL INPATH '/home/ubuntu/testtable.dat' OVERWRITE INTO TABLE testtable") ;
    //System.out.println("Load Data = " + loadData);
    
    // select * query
    TaskUnitResult<QueryResult> queryResult = client.executeQuery("SELECT count(*) AS count FROM testtable");
    System.out.println(queryResult);
    queryResult.getResult().dump(20) ;
  }
  
  
  
}