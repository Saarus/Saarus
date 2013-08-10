package org.saarus.service.sql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.saarus.service.util.TabularPrinter;
import org.saarus.util.text.CosineSimilarity;

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
  
  public String createTableSQL(String location) {
    StringBuilder b = new StringBuilder() ;
    b.append("CREATE TABLE ").append(getTableName()).append("(\n") ;
    List<FieldInfo> fields = getFields() ;
    for(int i = 0; i < fields.size(); i++) {
      FieldInfo field = fields.get(i) ;
      b.append("  ").append(field.getName()).append("  ").append(field.getType()) ;
      if(i < fields.size() - 1) b.append(",") ;
      b.append("\n") ;
    }
    b.append(") STORED AS RCFILE") ;
    if(location != null) {
      b.append(" LOCATION '").append(location).append("'") ;
    }
    return b.toString() ;
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
      List<KeyValue> properties = new ArrayList<KeyValue>() ;
      
      findProperties(properties, null, node) ;
      return autoDetectMapping(mdata, properties) ;
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return new String[0][3] ;
  }
  
  static public String[][] autoDetectMapping(TableMetadata mdata, String[] fieldNames) {
    try {
      List<KeyValue> properties = new ArrayList<KeyValue>() ;
      for(String sel : fieldNames) {
        properties.add(new KeyValue(sel, "")) ;
      }
      return autoDetectMapping(mdata, properties) ;
    } catch (Throwable e) {
      e.printStackTrace();
    }
    return new String[0][3] ;
  }
  
  static public String[][] autoDetectMapping(TableMetadata mdata, List<KeyValue> properties) {
    if(mdata == null) {
      String[][] mappingData = new String[properties.size()][] ;
      Iterator<KeyValue> i = properties.iterator() ;
      int entryIndex = 0 ;
      while(i.hasNext()) {
        KeyValue entry = i.next() ;
        String type = "" ;
        if(entry.value.matches("[a-zA-Z]")) type = "STRING" ;
        mappingData[entryIndex] = new String[] {entry.key, type, entry.key};
        entryIndex++ ;
      }
      return mappingData ;
    } else {
      List<FieldInfo> fieldInfos = mdata.getFields();
      String[][] mappingData = new String[fieldInfos.size()][] ;
      for(int i = 0; i < fieldInfos.size(); i++) {
        FieldInfo finfo = fieldInfos.get(i) ;
        KeyValue matchKeyValue = findMatch(properties, finfo.getName()) ;
        String matchKey = "" ;
        if(matchKeyValue != null) matchKey = matchKeyValue.key ;
        mappingData[i] = new String[] {finfo.getName(), finfo.getType(), matchKey} ;
      }
      return mappingData ;
    }
  }
  
  static KeyValue findMatch(List<KeyValue> keyValues, String key) {
    double bestSimilarity = 0d ;
    KeyValue bestKV = null ;
    for(int i = 0; i < keyValues.size(); i++) {
      KeyValue kv = keyValues.get(i) ;
      double similarity = CosineSimilarity.INSTANCE.similarity(key.toCharArray(), kv.key.toCharArray()) ;
      if(similarity > bestSimilarity) {
        bestSimilarity = similarity ;
        bestKV = kv ;
      }
    }
    return bestKV ;
  }
  
  static private void findProperties(List<KeyValue> holder, String field, JsonNode node) {
    if(node.isValueNode()) {
      holder.add(new KeyValue(field, node.asText())) ;
    } else {
      Iterator<Map.Entry<String, JsonNode>> i = node.getFields() ;
      while(i.hasNext()) {
        Map.Entry<String, JsonNode> entry = i.next() ;
        String fname = entry.getKey() ;
        if(field != null) fname = field + "." + fname ;
        findProperties(holder, fname, entry.getValue()) ;
      }
    }
  }
  
  static class KeyValue {
    String key, value ;
    
    KeyValue(String key, String value) {
      this.key = key ;
      this.value = value ;
    }
  }
}
