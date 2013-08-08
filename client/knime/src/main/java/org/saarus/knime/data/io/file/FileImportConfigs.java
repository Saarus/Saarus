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
import org.saarus.service.task.Task;
import org.saarus.service.task.TaskUnit;
import org.saarus.util.json.JSONSerializer;

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
      String json = settings.getString(PREFIX + selName) ;
      try {
        FileImportConfig importConfig = JSONSerializer.JSON_SERIALIZER.fromString(json, FileImportConfig.class);
        fileSettings.put(selName, importConfig) ;
      } catch (IOException e) {
        throw new RuntimeException(e) ;
      }
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
      try {
        String json = JSONSerializer.JSON_SERIALIZER.toString(sel);
        settings.addString(PREFIX  + sel.table, json) ;
      } catch (IOException e) {
        throw new RuntimeException(e) ;
      }
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

  public Task generatedTask() throws IOException {
    Task task = new Task() ;
    List<TaskUnit> units = new ArrayList<TaskUnit>() ;
    Iterator<FileImportConfig> i = fileSettings.values().iterator() ;
    while(i.hasNext()) {
      FileImportConfig config = i.next() ;
      for(TaskUnit sel : config.generateTaskUnits()) {
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
    boolean createNew = true;
    List<FieldConfig> fieldConfigs = new ArrayList<FieldConfig>() ;

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
    public void setDescription(String string) { this.description = string ; }
    
    public String getFile() { return file; }
    public void setFile(String file) { this.file = file ; }
    
    public String getImportType() { return this.importType ; }
    public void setImportType(String type) { this.importType = type ;} 
    
    public boolean isCreateNew() { return this.createNew ; }
    public void    setCreateNew(boolean b) { this.createNew = b ; }
    
    public void addFieldConfig(String fname, String ftype, String recordProperty) {
      fieldConfigs.add(new FieldConfig(fname, ftype, recordProperty)) ;
    }

    public List<FieldConfig> getFieldConfigs() { return this.fieldConfigs ; }
    public void setFieldConfigs(List<FieldConfig> configs) { this.fieldConfigs = configs ; }
    
    public String[][] fieldMappingConfigTable() {
      String[][] fieldConfig =new String[fieldConfigs.size()][] ;
      for(int i = 0; i < fieldConfig.length; i++) {
        fieldConfig[i] = fieldConfigs.get(i).cellValues() ;
      }
      return fieldConfig ;
    }

    public TaskUnit[] generateTaskUnits() {
      String[][] fieldMappingConfig = fieldMappingConfigTable() ;

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

  static public class FieldConfig {
    private String fieldName ;
    private String type ;
    private String mapProperty ;

    public FieldConfig() {} 

    public FieldConfig(String name, String type, String mapProperty) {
      this.fieldName = name ;
      this.type = type ;
      this.mapProperty = mapProperty ;
    }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMapProperty() { return mapProperty; }
    public void setMapProperty(String mapProperty) { this.mapProperty = mapProperty; }

    public String[] cellValues() { 
      return new String[] { fieldName, type, mapProperty }  ; 
    }
  }
}
