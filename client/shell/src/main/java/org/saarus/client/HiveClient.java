package org.saarus.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.saarus.service.sql.TableMetadata;
import org.saarus.service.task.TaskUnitResult;
import org.springframework.web.client.RestTemplate;


public class HiveClient {
  private RestTemplate restTemplate;
  private String restUrl ;

  private List<String> availableTables ;
  private Map<String, TableMetadata> tableMetadatas = new HashMap<String, TableMetadata>() ;

  public HiveClient() { }

  public HiveClient(RestTemplate restTemplate, String restUrl) {
    this.restTemplate = restTemplate ;
    this.restUrl = restUrl ;
  }

  public RestTemplate getRestTemplate() { return restTemplate; }
  public void setRestTemplate(RestTemplate restTemplate) { this.restTemplate = restTemplate; }

  public String getRestUrl() { return restUrl; }
  public void setRestUrl(String restUrl) { this.restUrl = restUrl; }

  public void clearCache() {
    availableTables = null ;
    tableMetadatas.clear() ;
  }

  public List<String> listTable(boolean forceUpdate) {
    if(forceUpdate) {
      availableTables = null ;
    }
    if(availableTables == null) {
      TaskUnitResult<List<String>> listResult = 
          restTemplate.getForObject(restUrl + "/hive/table/list?forceUpdate=true", TaskUnitResult.class);
      availableTables = listResult.getResult() ;
    }
    return availableTables;
  }

  public TableMetadata descTable(String table, boolean forceUpdate) {
    if(forceUpdate) tableMetadatas.remove(table) ;
    TableMetadata tmetadata = tableMetadatas.get(table) ;
    if(tmetadata == null) {
      TaskUnitResult<TableMetadata> tableResult = 
          restTemplate.getForObject(restUrl + "/hive/table/desc/" + table, TaskUnitResult.class);
      tmetadata =  tableResult.getResult() ;
      tableMetadatas.put(table, tmetadata) ;
    }
    return tmetadata ;
  }
}