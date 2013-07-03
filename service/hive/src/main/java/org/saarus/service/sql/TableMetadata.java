package org.saarus.service.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.saarus.service.util.TabularPrinter;

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
  
  static public String[][] autoDetectMapping(TableMetadata mdata, JsonNode node) {
    try {
      Map<String, String> properties = new LinkedHashMap<String, String>() ;
      findProperties(properties, null, node) ;
      if(mdata == null) {
        String[][] mappingData = new String[properties.size()][] ;
        Iterator<Map.Entry<String, String>> i = properties.entrySet().iterator() ;
        int entryIndex = 0 ;
        while(i.hasNext()) {
          Map.Entry<String, String> entry = i.next() ;
          String property = entry.getKey() ;
          String value = entry.getValue() ;
          String type = "" ;
          if(value.matches("[a-zA-Z]")) type = "STRING" ;
          int idx = property.lastIndexOf('.') ;
          if(idx < 0) {
            mappingData[entryIndex] = new String[] {property, type, property};
          } else {
            String fieldName = property.replace('.', '_') ;
            mappingData[entryIndex] = new String[] {fieldName, type, property};
          }
          entryIndex++ ;
        }
        return mappingData ;
      } else {
        List<FieldInfo> fieldInfos = mdata.getFields();
        String[][] mappingData = new String[fieldInfos.size()][] ;
        for(int i = 0; i < fieldInfos.size(); i++) {
          FieldInfo finfo = fieldInfos.get(i) ;
          String mapProperty = finfo.getName();
          if(!properties.containsKey(mapProperty)) mapProperty = "" ;
          mappingData[i] = new String[] {finfo.getName(), finfo.getType(), mapProperty} ;
        }
        return mappingData ;
      }
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return new String[0][3] ;
  }
  
  static private void findProperties(Map<String, String> holder, String field, JsonNode node) {
    if(node.isValueNode()) {
      holder.put(field, node.asText()) ;
    } else {
      Iterator<Map.Entry<String, JsonNode>> i = node.getFields() ;
      while(i.hasNext()) {
        Map.Entry<String, JsonNode> entry = i.next() ;
        String fname = entry.getKey() ;
        if(field != null) fname = field + "_" + fname ;
        findProperties(holder, fname, entry.getValue()) ;
      }
    }
    
  }
}
