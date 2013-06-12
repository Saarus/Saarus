package org.saarus.knime.data.in.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.config.ConfigRO;
import org.knime.core.node.config.ConfigWO;
import org.saarus.service.task.Task;
import org.saarus.service.task.TaskUnit;

public class JSONImportConfigs {
  final static String PREFIX = "file:" ;
  final static String IMPORT_NAMES = PREFIX + "import.names" ;
  
  private Map<String, JSONImportConfig> fileSettings = new LinkedHashMap<String, JSONImportConfig>() ;
 
  public JSONImportConfigs() {}
  
  public JSONImportConfigs(NodeSettingsRO settings) throws InvalidSettingsException {
    if(!settings.containsKey(IMPORT_NAMES)) return ;
    String names = settings.getString(IMPORT_NAMES) ;
    String[] name = names.split(",") ;
    for(String selName : name) {
      ConfigRO config = settings.getConfig(PREFIX + selName) ;
      JSONImportConfig fsetting = 
          new JSONImportConfig(config.getString("table"), config.getString("description"), config.getString("path")) ;
      fileSettings.put(selName, fsetting) ;
    }
  }
  
  public void saveSettings(NodeSettingsWO settings) {
    if(fileSettings.size() == 0) return ;
    
    StringBuilder names = new StringBuilder() ;
    Iterator<JSONImportConfig> i = fileSettings.values().iterator() ;
    while(i.hasNext()) {
      JSONImportConfig sel = i.next() ;
      if(names.length() > 0) names.append(",") ;
      names.append(sel.table) ;
      ConfigWO config = settings.addConfig(PREFIX  + sel.table) ;
      config.addString("table", sel.table) ;
      config.addString("description", sel.description) ;
      config.addString("path", sel.path) ;
    }
    settings.addString(IMPORT_NAMES, names.toString()) ;
  }
  
  public void merge(JSONImportConfigs other) {
    System.out.println("Current setting has " + fileSettings.size() + " config");
    System.out.println("Other setting has " + fileSettings.size() + " config");
    
    Iterator<JSONImportConfig> i = other.fileSettings.values().iterator() ;
    while(i.hasNext()) {
      JSONImportConfig sel = i.next() ;
      fileSettings.put(sel.table, sel) ;
    }
  }
  
  public void add(String name, String desc, String path) {
    fileSettings.put(name, new JSONImportConfig(name, desc, path)) ;
  }
  
  public void add(JSONImportConfig config) {
    fileSettings.put(config.table, config) ;
  }
  
  public Collection<JSONImportConfig>  getFileImportConfig() { return fileSettings.values() ; }
  
  public Task getGeneratedTask() throws IOException {
    Task task = new Task() ;
    List<TaskUnit> units = new ArrayList<TaskUnit>() ;
    Iterator<JSONImportConfig> i = fileSettings.values().iterator() ;
    while(i.hasNext()) {
      JSONImportConfig config = i.next() ;
      for(TaskUnit sel : config.getGeneratedTaskUnits()) {
        units.add(sel) ;
      }
    }
    task.setTaskHandler("HiveService") ;
    task.setTaskUnits(units) ;
    task.setTaskSubmitWait(3000l) ;
    task.setDescription("create json tables") ;
    return task ;
  }
  
  public String toString() {
    StringBuilder b = new StringBuilder() ;
    b.append("Config:\n") ;
    Iterator<JSONImportConfig> i = fileSettings.values().iterator() ;
    while(i.hasNext()) {
      JSONImportConfig sel = i.next() ;
      b.append("Name = ").append(sel.table).
        append(", Desc = ").append(sel.description).
        append(", Path = ").append(sel.path).append("\n") ;
    }
    return b.toString() ;
  }
  
  static public class JSONImportConfig {
    String table = "TableName";
    String description = "Import json file as a hive table using json serializer and deserializer";
    String path = "";
    
    public JSONImportConfig() {} 
    
    JSONImportConfig(String name, String desc, String path) {
      this.table = name ;
      this.description = desc ;
      this.path = path ;
    }

    public String getTable() { return table; }
    public void setTable(String name) { this.table = name ; }
    
    public String getDescription() { return description ; }

    public String getPath() { return path; }
    
    public TaskUnit[] getGeneratedTaskUnits() {
      TaskUnit dropTask = new TaskUnit() ;
      dropTask.setName("execute") ;
      dropTask.setDescription("drop json table " + table) ;
      String dropTable = "DROP TABLE if exists %1s" ;
      dropTask.setTaskLine(String.format(dropTable, table)) ;
      
      String createTable = "CREATE EXTERNAL TABLE %1s (json STRING)  LOCATION '%2s'" ;
      TaskUnit createTask = new TaskUnit() ;
      createTask.setDescription("create json table " + table) ;
      createTask.setName("execute") ;
      createTask.setTaskLine(String.format(createTable, table, path)) ;
      return new TaskUnit[] { dropTask, createTask } ;
    }
  }
}
