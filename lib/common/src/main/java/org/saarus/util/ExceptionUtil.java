package org.saarus.util;

import java.util.regex.Pattern;
/**
 * $Author: Tuan Nguyen$ 
 **/
public class ExceptionUtil {
  static public String getStackTrace(Throwable t, String filterExp) {
    Pattern filter = null ;
    if(filterExp != null) Pattern.compile(filterExp, Pattern.CASE_INSENSITIVE) ;
    StringBuilder b = new StringBuilder() ;
    b.append(t.getMessage()).append("\n") ;
    StackTraceElement[] elements = t.getStackTrace();
    for(StackTraceElement element : elements) {
      String eleString = element.toString() ;
      if(filter != null && !filter.matcher(eleString).matches()) continue ;
      b.append(element.toString()).append("\n") ;
    }
    return b.toString() ;
  }
  
  static public String getStackTrace(StackTraceElement[] elements, String filterExp) {
    Pattern filter = null ;
    if(filterExp != null) filter = Pattern.compile(filterExp, Pattern.CASE_INSENSITIVE) ;
    StringBuilder b = new StringBuilder() ;
    for(StackTraceElement element : elements) {
      String eleString = element.toString() ;
      if(filter != null && !filter.matcher(eleString).matches()) continue ;
      b.append(element.toString()).append("\n") ;
    }
    return b.toString() ;
  }
}