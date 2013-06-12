package org.saarus.service.task;

import java.util.HashMap;

public class Parameters extends HashMap<String, String> {
  public String getString(String name, String dvalue) {
    String val = get(name) ;
    if(val != null) return val ;
    return dvalue ;
  }
  
  public void setString(String name, String value) {
    put(name, value) ;
  }
  
  public int getInteger(String name, int dvalue) {
    String val = get(name) ;
    if(val != null) return Integer.parseInt(val) ;
    return dvalue ;
  }
  
  public void setInteger(String name, int value) {
    put(name, Integer.toString(value)) ;
  }
  
  public long getLong(String name, long dvalue) {
    String val = get(name) ;
    if(val != null) return Long.parseLong(val) ;
    return dvalue ;
  }
  
  public void setLong(String name, long value) {
    put(name, Long.toString(value)) ;
  }
}
