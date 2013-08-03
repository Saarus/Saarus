package org.saarus.service.sql.io;

public interface Progressable {
  public void progress(String[] properties, String[] val) ;
  
  static public class DebugProgressable implements Progressable {
    public void progress(String[] properties, String[] val) {
      StringBuilder b = new StringBuilder() ;
      for(int i = 0; i < val.length; i++) {
        if(i > 0 ) b.append(", ") ;
        b.append(properties[i]).append("=").append(val[i]);
      }
      System.out.println(b);
    }
  }
}