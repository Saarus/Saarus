package org.saarus.service.sql.io;

import java.io.InputStream;

import org.codehaus.jackson.JsonNode;
import org.saarus.service.util.JSONReader;

public class JSONImporter {
  private TableWriter writer ;
  private Progressable progressable ;
  
  public JSONImporter(TableWriter writer, Progressable progressable) throws Exception {
    this.writer = writer ;
    this.progressable = progressable ;
  }
  
  public void doImport(InputStream is, String[] properties) throws Exception {
    doImport(new JSONReader(is), properties) ;
  }
  
  
  public void doImport(String jsonFile, String[] properties) throws Exception {
    doImport(new JSONReader(jsonFile), properties) ;
  }
  
  public void doImport(JSONReader reader, String[] properties) throws Exception {
    JsonNode node = null ;
    JSONPropertiesReader preader = new JSONPropertiesReader() ;
    while(((node = reader.read()) != null)) {
      String[] value = preader.read(node, properties) ;
      if(progressable != null) progressable.progress(properties, value) ;
      writer.writeRow(value) ;
    }
  }
  
  public void close() throws Exception {
    writer.close() ;
  }
  
  public class JSONPropertiesReader {
    public String[] read(JsonNode node, String[] property) {
      String[] value = new String[property.length] ;
      for(int i = 0; i < value.length; i++) {
        value[i] = getProperty(node, property[i], null) ;
      }
      return value ;
    }

    String getProperty(JsonNode node, String s, String dval) {
      if(s.indexOf(".") < 0) {
        JsonNode fnode = node.get(s) ;
        if(fnode == null) return dval ;
        String retVal = fnode.asText() ;
        if(retVal == null) return dval ;
        return retVal ;
      } else {
        String[] field = s.split("\\.");
        int idx = 0 ;
        while(idx < field.length) {
          String n = field[idx];
          JsonNode current = node.get(n) ;
          idx++ ;
          if(current == null) return dval ;
          if(idx == field.length) return current.asText() ;
          node = current ;
        }
        return dval;
      }
    }
  }
}