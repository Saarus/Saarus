package org.saarus.service.hive;

import java.util.ArrayList;
import java.util.List;

public class TableMetadata {
  private String tableName ;
  private List<FieldInfo> fields = new ArrayList<FieldInfo>() ;
  
  public TableMetadata() {} 
  
  public TableMetadata(String name) {
    this.tableName = name ;
  }
  
  public String getTableName() { return tableName; }
  public void setTableName(String tableName) { this.tableName = tableName; }

  public List<FieldInfo> getFields() { return fields; }
  public void setFields(List<FieldInfo> fields) { this.fields = fields; }
  
  public void addField(String name, String type) {
    fields.add(new FieldInfo(name, type)) ;
  }
  
  public String toString() {
    StringBuilder b = new StringBuilder() ;
    b.append("Table: ").append(tableName).append("\n") ;
    int[] width = {30, 30} ;
    String[] header = {"Name", "Type"} ;
    TabularPrinter printer = new TabularPrinter(b, width) ;
    printer.printHeader(header) ;
    for(int i = 0; i < fields.size(); i++) {
      FieldInfo fi = fields.get(i) ;
      String[] column = {fi.getName(), fi.getType()} ;
      printer.printRow(column, false) ;
    }
    return b.toString() ;
  }
  
  static public class FieldInfo {
    private String name ;
    private String type ;
    
    public FieldInfo() {}
    
    public FieldInfo(String name, String type) {
      this.name = name ;
      this.type = type ;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
  }
}
