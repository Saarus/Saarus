package org.saarus.mahout.classifier.sgd;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CVSDataSorter {
  
  public void sort(String file) throws Exception {
    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file))) ;
    PrintStream out = new PrintStream(new FileOutputStream(file + ".sort")) ;
    String header = in.readLine() ;
    List<String> lines = new ArrayList<String>() ;
    String line = null ;
    while((line = in.readLine()) != null) {
      lines.add(line) ;
    }
    in.close() ;

    out.append(header);
    Collections.sort(lines) ;
    for(int i = lines.size() - 1; i >=0 ; i--) {
      out.append("\n") ;
      out.append(lines.get(i)) ;
    }
    out.close() ;
  }

  public void normalize(String file) throws Exception {
    BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file))) ;
    PrintStream out = new PrintStream(new FileOutputStream(file + ".norm")) ;
    String header = in.readLine() ;
    out.append(header.trim());
    String line = null ;
    while((line = in.readLine()) != null) {
      out.append("\n") ;
      out.append(line.trim()) ;
    }
    in.close() ;
  }

  
  static public void main(String[] args) throws Exception {
    CVSDataSorter sorter = new CVSDataSorter() ;
    sorter.sort("yelp_select_features_1.csv") ;
    sorter.sort("yelp_select_features_2.csv") ;
    sorter.normalize("yelp_select_features_2.csv") ;
  }
}
