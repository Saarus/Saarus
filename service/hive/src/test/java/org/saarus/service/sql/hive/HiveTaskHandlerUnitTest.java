package org.saarus.service.sql.hive;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.saarus.service.hadoop.HadoopInfo;
import org.saarus.service.sql.SQLService;
import org.saarus.service.sql.QueryResult;
import org.saarus.service.sql.TableMetadata;
import org.saarus.service.sql.hive.HiveTaskHandler;
import org.saarus.service.task.TaskUnitResult;

public class HiveTaskHandlerUnitTest {
  @Test
  public void testClient() throws Exception {
    SQLService hservice  = new SQLService(HadoopInfo.HIVE_CONNECTION_URL, "hive", "");
    HiveTaskHandler handler  = new HiveTaskHandler(hservice);

    String tableName = "testtable";
    System.out.println(handler.dropTable(tableName)) ;
    handler.execute("CREATE EXTERNAL TABLE " + tableName + " (key INT, value STRING) location '/user/hive/testdb/testtable'");
    // show tables
    TaskUnitResult<List<String>> listResult = handler.listTables() ;
    System.out.println(listResult);
    System.out.println("Available Tables: ");
    for(String selTable : listResult.getResult()) {
      System.out.println("  " + selTable);
    }
    // describe table
    TaskUnitResult<TableMetadata> descResult = handler.describeTable(tableName) ;
    System.out.println(descResult);
    
    String inserSql = "insert into testtable (key, value) values (?, ?)" ;
    List<Object[]> paramHolder = new ArrayList<Object[]>() ;
    for(int i = 0; i < 100; i++) {
      paramHolder.add(new Object[] {i, "value " + i}) ;
    }
    hservice.insert(inserSql, paramHolder) ;
    
    //boolean loadData = 
    //    client.execute("LOAD DATA LOCAL INPATH '/home/ubuntu/testtable.dat' OVERWRITE INTO TABLE testtable") ;
    //System.out.println("Load Data = " + loadData);
    
    // select * query
    TaskUnitResult<QueryResult> queryResult = handler.executeQuery("SELECT count(*) AS count FROM testtable");
    System.out.println(queryResult);
    queryResult.getResult().dump(20) ;
  }
}