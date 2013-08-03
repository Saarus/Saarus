package org.saarus.service.sql;

import java.sql.ResultSet;

import org.apache.hadoop.conf.Configuration;
import org.codehaus.jackson.JsonNode;
import org.junit.Test;
import org.saarus.service.hadoop.HadoopInfo;
import org.saarus.service.hadoop.util.HDFSUtil;
import org.saarus.service.sql.io.TableRCFileWriter;
import org.saarus.service.util.JSONSerializer;
import org.saarus.service.util.TabularPrinter;

public class SQLServiceUnitTest {
  @Test
  public void testClient() throws Exception {
    SQLService hservice  = new SQLService(HadoopInfo.HIVE_CONNECTION_URL, "hive", "");
    
    String tableName = "testtable";
    String location = "/tmp/testdb/testtable" ;
    
    TableMetadata tmeta = new TableMetadata(tableName) ;
    tmeta.addField("key", "INT") ;
    tmeta.addField("value", "STRING") ;
    SQLTable sqlTable = new SQLTable(hservice, tmeta) ;
    System.out.println("Drop Table: " + sqlTable.dropTable()) ;
    
    sqlTable.createTable(location) ;
    
    //hservice.executeSQL(String.format("CREATE EXTERNAL TABLE %s (key INT, value STRING) STORED AS RCFILE LOCATION '%s'", tableName, location));
    
    Configuration conf = HDFSUtil.getConfiguration() ;
    TableRCFileWriter writer = sqlTable.createTableWriter(conf, "data0.rcfile") ;
    for(int i = 0; i < 100; i++) {
      writer.writeRow(Integer.toString(i), "value " + i) ;
    }
    writer.close() ;
    
    writer = sqlTable.createTableWriter(conf, "data1.rcfile") ;
    for(int i = 100; i < 200; i++) {
      writer.writeRow(Integer.toString(i), "value " + i) ;
    }
    writer.close() ;
    
    ResultSet rset = hservice.executeQuerySQL(String.format("SELECT * FROM %s", tableName)) ;
    while(rset.next()) {
      System.out.println(String.format("key = %s, value = %s", rset.getObject(1), rset.getObject(2)));
    }
    rset.close() ;

    String jsonData = "{\"key\": 1, \"value\": \"string value\"}" ;
    JsonNode jsonDataNode = JSONSerializer.JSON_SERIALIZER.fromString(jsonData) ;
    TableMetadata tmetadata = hservice.descTable(tableName) ;
    String[][] mappingData = TableMetadata.autoDetectMapping(tmetadata, jsonDataNode) ;
    printJSONDataMapping(mappingData) ;
  }
  
  private void printJSONDataMapping(String[][] mappingData) {
    TabularPrinter printer = new TabularPrinter(new int[] {20, 20, 20}) ;
    for(int i = 0; i < mappingData.length; i++) {
      printer.printRow((Object[])mappingData[i]) ;
    }
  }
}