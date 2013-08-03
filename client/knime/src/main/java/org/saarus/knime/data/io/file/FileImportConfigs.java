package org.saarus.knime.data.io.file;

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

public class FileImportConfigs {
  final static String PREFIX = "file:" ;
  final static String IMPORT_NAMES = PREFIX + "import.names" ;
  
  private Map<String, FileImportConfig> fileSettings = new LinkedHashMap<String, FileImportConfig>() ;
 
  public FileImportConfigs() {}
  
  public FileImportConfigs(NodeSettingsRO settings) throws InvalidSettingsException {
    if(!settings.containsKey(IMPORT_NAMES)) return ;
    String names = settings.getString(IMPORT_NAMES) ;
    String[] name = names.split(",") ;
    for(String selName : name) {
      ConfigRO config = settings.getConfig(PREFIX + selName) ;
      String table = config.getString("table") ;
      String desc = config.getString("description") ;
      String file = config.getString("file") ;
      String importType = config.getString("importType") ;
      FileImportConfig importConfig = new FileImportConfig(table, desc, file, importType) ;
      importConfig.fieldConfigs = config.getString("fieldConfigs") ;
      fileSettings.put(selName, importConfig) ;
    }
  }
  
  public void saveSettings(NodeSettingsWO settings) {
    if(fileSettings.size() == 0) return ;
    
    StringBuilder names = new StringBuilder() ;
    Iterator<FileImportConfig> i = fileSettings.values().iterator() ;
    while(i.hasNext()) {
      FileImportConfig sel = i.next() ;
      if(names.length() > 0) names.append(",") ;
      names.append(sel.table) ;
      ConfigWO config = settings.addConfig(PREFIX  + sel.table) ;
      config.addString("table", sel.table) ;
      config.addString("description", sel.description) ;
      config.addString("file", sel.file) ;
      config.addString("importType", sel.importType) ;
      config.addString("fieldConfigs", sel.fieldConfigs) ;
    }
    settings.addString(IMPORT_NAMES, names.toString()) ;
  }
  
  public void merge(FileImportConfigs other) {
    System.out.println("Current setting has " + fileSettings.size() + " config");
    System.out.println("Other setting has " + fileSettings.size() + " config");
    
    Iterator<FileImportConfig> i = other.fileSettings.values().iterator() ;
    while(i.hasNext()) {
      FileImportConfig sel = i.next() ;
      fileSettings.put(sel.table, sel) ;
    }
  }
  
  public void addConfig(FileImportConfig config) {
    fileSettings.put(config.table, config) ;
  }
  
  public Collection<FileImportConfig>  getFileImportConfig() { return fileSettings.values() ; }
  
  public Task getGeneratedTask() throws IOException {
    Task task = new Task() ;
    List<TaskUnit> units = new ArrayList<TaskUnit>() ;
    Iterator<FileImportConfig> i = fileSettings.values().iterator() ;
    while(i.hasNext()) {
      FileImportConfig config = i.next() ;
      for(TaskUnit sel : config.getGeneratedTaskUnits()) {
        units.add(sel) ;
      }
    }
    task.setTaskHandler("SQLService") ;
    task.setTaskUnits(units) ;
    task.setTaskSubmitWait(3000l) ;
    task.setDescription("create tables") ;
    return task ;
  }
  
  public String toString() {
    StringBuilder b = new StringBuilder() ;
    b.append("Config:\n") ;
    Iterator<FileImportConfig> i = fileSettings.values().iterator() ;
    while(i.hasNext()) {
      FileImportConfig sel = i.next() ;
      b.append("Name = ").append(sel.table).
        append(", Desc = ").append(sel.description).
        append(", File = ").append(sel.file).append("\n") ;
    }
    return b.toString() ;
  }
  
  static public class FileImportConfig {
    String table = "TableName";
    String description = "Import file into a hive table";
    String file = "";
    String importType = "";
    String fieldConfigs ;
    
    public FileImportConfig() {} 
    
    FileImportConfig(String name, String desc, String path, String importType) {
      this.table = name ;
      this.description = desc ;
      this.file = path ;
      this.importType = importType; 
    }

    public String getTable() { return table; }
    public void setTable(String name) { this.table = name ; }
    
    public String getDescription() { return description ; }

    public String getFile() { return file; }
    
    public String getImportType() { return this.importType ; }
    
    public void addFieldConfig(String fname, String ftype, String recordProperty) {
      if(fieldConfigs == null) {
        fieldConfigs = fname + "," + ftype + "," + recordProperty ;
      } else {
        fieldConfigs += "\n" + fname + "," + ftype + "," + recordProperty ;
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
      dropTask.setDescription("drop table " + table) ;
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
      
      TaskUnit importFileUnit = new TaskUnit() ;
      String[] properties = new String[fieldMappingConfig.length] ;
      for(int i = 0; i < properties.length; i++) {
        properties[i] = fieldMappingConfig[i][2] ;
      }
      importFileUnit.setName("import" + importType) ;
      importFileUnit.getParameters().setString("dbfile", "/user/hive/warehouse/" + table +  "/data0.rcfile") ;
      importFileUnit.getParameters().setString("file", this.file) ;
      importFileUnit.getParameters().setStringArray("properties", properties) ;
      
      return new TaskUnit[] { dropTask, createTask, importFileUnit } ;
    }
  }
}
