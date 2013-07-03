package org.saarus.service.task;

import java.util.LinkedHashMap;

import org.saarus.service.util.StringUtil;

public class Parameters extends LinkedHashMap<String, Object> {
  private static final long serialVersionUID = 1L;

  public String getString(String name) {
    Object val = get(name) ;
    if(val != null) return (String)val ;
    return null ;
  }
  
  public String getString(String name, String dvalue) {
    Object val = get(name) ;
    if(val != null) return (String) val ;
    return dvalue ;
  }
  
  public void setString(String name, String value) {
    put(name, value) ;
  }
  
  public String[] getStringArray(String name, String[] dvalue) {
    Object val = get(name) ;
    if(val != null) {
      String string = (String) val ;
      return StringUtil.toStringArray(string) ;
    }
    return dvalue ;
  }
  
  public void setStringArray(String name, String[] array) {
    if(array == null) return ;
    put(name, StringUtil.joinStringArray(array)) ;
  }
  
  public int getInteger(String name, int dvalue) {
    Integer val = (Integer) get(name) ;
    if(val != null) return (Integer)val ;
    return dvalue ;
  }
  
  public void setInteger(String name, int value) {
    put(name, value) ;
  }
  
  public long getLong(String name, long dvalue) {
    Object val =  get(name) ;
    if(val instanceof Integer) return new Long(((Integer)val)) ;
    if(val != null) return (Long) val ;
    return dvalue ;
  }
  
  public void setLong(String name, long value) {
    put(name, value) ;
  }
  
  public Object getObject(String name) {
    return get(name) ;
  }
  
  public Object getObject(String name, Object dval) {
    Object val = get(name) ;
    if(val == null) return dval ;
    return val ;
  }
  
  public void setObject(String name, Object obj) {
    put(name, obj) ;
  }
}
