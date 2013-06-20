package org.saarus.mahout.classifier.sgd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class CVSDataTransformer {
  BufferedReader in ;
  private Header[] header ;
  
  public CVSDataTransformer(String file) throws Exception {
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
    //String file = "review-test.csv" ;
    String file = "yelp_select_features_2.csv" ;
    CVSDataTransformer parser = new CVSDataTransformer(file) ;
    PrintStream out = new PrintStream(new FileOutputStream(file + ".transform")) ;
    out.append("stars,business_review_count,vote_useful") ;
    Map<String, String> record ;
    while((record = parser.next()) != null) {
      out.append("\n") ;
      double stars = Double.parseDouble(record.get("stars"))/5 ;
      double business_review_count = Double.parseDouble(record.get("business_review_count")) ;
      if(business_review_count > 500) business_review_count = 500 ;
      business_review_count = business_review_count/500 ;
      int vote_useful = Integer.parseInt(record.get("vote_useful")) ;
      if(vote_useful >= 1) vote_useful = 1 ;
      else vote_useful = 0 ;
      out.append(Double.toString(stars)).append(",").
          append(Double.toString(business_review_count)).append(",").
          append(Integer.toString(vote_useful)) ;
    }
  }
}
