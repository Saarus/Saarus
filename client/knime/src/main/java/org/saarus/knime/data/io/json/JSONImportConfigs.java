package org.saarus.knime.data.io.json;

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
import org.saarus.service.util.StringUtil;

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
      JSONImportConfig importConfig = 
          new JSONImportConfig(config.getString("table"), config.getString("description"), config.getString("jsonFile")) ;
      importConfig.fieldConfigs = config.getString("fieldConfigs") ;
      fileSettings.put(selName, importConfig) ;
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
      config.addString("jsonFile", sel.jsonFile) ;
      config.addString("fieldConfigs", sel.fieldConfigs) ;
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
  
  public void addConfig(JSONImportConfig config) {
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
    task.setTaskHandler("SQLService") ;
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
        append(", Json File = ").append(sel.jsonFile).append("\n") ;
    }
    return b.toString() ;
  }
  
  static public class JSONImportConfig {
    String table = "TableName";
    String description = "Import json file as a hive table using json serializer and deserializer";
    String jsonFile = "";
    String fieldConfigs ;
    
    public JSONImportConfig() {} 
    
    JSONImportConfig(String name, String desc, String path) {
      this.table = name ;
      this.description = desc ;
      this.jsonFile = path ;
    }

    public String getTable() { return table; }
    public void setTable(String name) { this.table = name ; }
    
    public String getDescription() { return description ; }

    public String getJsonFile() { return jsonFile; }
    
    public void addFieldConfig(String fname, String ftype, String jsonProperty) {
      if(fieldConfigs == null) {
        fieldConfigs = fname + "," + ftype + "," + jsonProperty ;
      } else {
        fieldConfigs += "\n" + fname + "," + ftype + "," + jsonProperty ;
      }
    }
    
    public String[][] getFieldMappingConfig() {
      if(fieldConfigs == null || fieldConfigs.length() == 0) return new String[0][0] ;
      String[] lines = fieldConfigs.split("\n") ;
      String[][] fieldConfig =new String[lines.length][] ;
      for(int i = 0; i < lines.length; i++) {
        fieldConfig[i] = lines[i].split(",") ;
      }
      return fieldConfig ;
    }
    
    public TaskUnit[] getGeneratedTaskUnits() {
      String[][] fieldMappingConfig = getFieldMappingConfig() ;
      
      TaskUnit dropTask = new TaskUnit() ;
      dropTask.setName("execute") ;
      dropTask.setDescription("drop json table " + table) ;
      String dropTable = "DROP TABLE if exists %1s" ;
      dropTask.setTaskLine(String.format(dropTable, table)) ;
      
      StringBuilder createTable = new StringBuilder() ;
      createTable.append("CREATE TABLE ").append(this.table).append("(") ;
      
      for(int i = 0; i < fieldMappingConfig.length; i++) {
        if(i > 0) createTable.append(", ") ;
        createTable.append(fieldMappingConfig[i][0] + " " + fieldMappingConfig[i][1]);
      }
      createTable.append(") STORED AS RCFILE LOCATION '/user/hive/warehouse/" + table +"'") ;
      //createTable.append(") ROW FORMAT DELIMITED FIELDS TERMINATED BY \",\" ESCAPED BY '\\\\' STORED AS TEXTFILE LOCATION '/user/hive/warehouse/" + table +"'") ;
      TaskUnit createTask = new TaskUnit() ;
      createTask.setDescription("create table " + table) ;
      createTask.setName("execute") ;
      createTask.setTaskLine(createTable.toString()) ;
      
      TaskUnit importJsonUnit = new TaskUnit() ;
      String[] properties = new String[fieldMappingConfig.length] ;
      for(int i = 0; i < properties.length; i++) {
        properties[i] = fieldMappingConfig[i][2] ;
      }
      importJsonUnit.setName("importJson") ;
      importJsonUnit.getParameters().setString("dbfile", "/user/hive/warehouse/" + table +  "/data0.rcfile") ;
      importJsonUnit.getParameters().setString("jsonFile", this.jsonFile) ;
      importJsonUnit.getParameters().setStringArray("properties", properties) ;
      
      return new TaskUnit[] { dropTask, createTask, importJsonUnit } ;
    }
  }
}
