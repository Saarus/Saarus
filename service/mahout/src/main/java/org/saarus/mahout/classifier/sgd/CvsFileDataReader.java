package org.saarus.mahout.classifier.sgd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.base.CharMatcher;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class CvsFileDataReader implements DataReader {
  private static final Splitter COMMA = Splitter.on(',').trimResults(CharMatcher.is('"'));
  
  private String file ;
  private boolean readHeaderNames = true;
  
  private BufferedReader reader  ;
  private List<String> headerNames ;
  
  public CvsFileDataReader(String file, boolean readHeaderNames) throws Exception {
    this.file = file ;
    this.readHeaderNames = readHeaderNames ;
  }
  
  public List<String> getHeaderNames() { return this.headerNames ; }
  
  public List<String> nextRow() throws Exception {
    String line = reader.readLine() ;
    if(line == null) return null ;
    List<String> holder = Lists.newArrayList(COMMA.split(line)) ;
    return holder;
  }

  public void reset() throws Exception {
    reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
    if(readHeaderNames) {
      this.headerNames = nextRow() ;
    }
  }
  
  public void close() throws Exception {
    reader.close() ;
  }
  
}
