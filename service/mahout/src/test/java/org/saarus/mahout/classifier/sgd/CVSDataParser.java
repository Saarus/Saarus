package org.saarus.mahout.classifier.sgd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class CVSDataParser {
  BufferedReader in ;
  private Header[] header ;
  
  public CVSDataParser(String file) throws Exception {
    in = new BufferedReader(new InputStreamReader(new FileInputStream(file))) ;
    String firstLine = in.readLine() ;
    String[] headerCol = firstLine.split(",") ;
    header = new Header[headerCol.length] ;
    for(int i = 0; i < header.length; i++) {
      header[i] = new Header(headerCol[i].trim(), i) ;
    }
  }
  
  public Map<String, String> next() throws Exception {
    String line = in.readLine() ;
    if(line == null) return null ;
    String[] value = line.split(",") ;
    Map<String, String> map = new HashMap<String, String>() ;
    for(int i = 0; i < value.length; i++) {
      String name = header[i].name ;
      map.put(name, value[i]) ;
    }
    return map ;
  }
  
  static class Header {
    String name ;
    int    index ;
    
    public Header(String name, int index) {
      this.name = name ;
      this.index = index ;
    }
  }
  
  static public void main(String[] args) throws Exception {
    CVSDataParser parser = new CVSDataParser("src/test/resources/review-test.csv") ;
    PrintStream out = new PrintStream(new FileOutputStream("review-test.csv")) ;
    out.append("stars,business_review_count,vote_useful") ;
    Map<String, String> record ;
    while((record = parser.next()) != null) {
      out.append("\n") ;
      out.append(record.get("stars")).append(",").
          append(record.get("business_review_count")).append(",").
          append(record.get("vote_useful")) ;
    }
  }
}
