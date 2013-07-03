package org.saarus.service.sql.io;

import org.codehaus.jackson.JsonNode;

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