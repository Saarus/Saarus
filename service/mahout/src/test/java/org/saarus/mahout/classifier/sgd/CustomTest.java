package org.saarus.mahout.classifier.sgd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class CustomTest {
  @Test
  public void test() throws Exception {
    String[] headers = {"stars", "business_review_count", "useful"} ;
    
    selectData("src/test/resources/yelp/train.csv", "target/train.csv", headers) ;
    selectData("src/test/resources/yelp/test.csv", "target/test.csv", headers) ;
  
    String outputFile = "target/yelp.model";
    TrainLogistic tl = new TrainLogistic() ;
    tl.setMaxRead(1000) ;
    tl.train(new String[]{
        "--input", "target/train.csv",
        "--output", outputFile,
        "--target", "useful",
        "--categories", "2",
        "--predictors", "n:stars | n:business_review_count",
        "--features", "1000",
        "--passes", "100",
        "--rate", "50"
    }, new PrintWriter(System.out, true));

    new RunLogistic().predict(new String[]{
        "--input", "target/test.csv",
        "--model", outputFile,
        "--scores",
        "--auc",
        "--confusion", 
    }, new PrintWriter(System.out, true));
  }
  
  void selectData(String inFile, String outFile, String[] header) throws Exception {
    CVSDataParser parser = new CVSDataParser(inFile) ;
    PrintStream out = new PrintStream(new FileOutputStream(outFile)) ;
    for(int i = 0; i < header.length; i++) {
      if(i > 0) out.append(",") ;
      out.append(header[i]) ;
    }
    Map<String, String> record ;
    while((record = parser.next()) != null) {
      out.append("\n") ;
      for(int i = 0; i < header.length; i++) {
        if(i > 0) out.append(",") ;
        String cellValue = record.get(header[i]) ;
        if("useful".equals(header[i])) {
          //Do some tranform for cell value here base on the other value in record
          int stars = Integer.parseInt(record.get("stars")) ;
          int review_count = Integer.parseInt(record.get("business_review_count")) ;
          cellValue = "0" ;
          if(stars >3 && review_count >= 30) {
            
          }
        }
        out.append(cellValue) ;
      }
    }
  }
  
  static public class CVSDataParser {
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
  }
}
