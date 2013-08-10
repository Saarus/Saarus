package org.saarus.mahout.classifier.sgd;

import java.io.FileReader;
import java.util.HashMap;

import au.com.bytecode.opencsv.CSVReader;

public class CSVConverter {
  static public void main(String[] args) throws Exception {
    CSVReader csvReader = new CSVReader(new FileReader("src/test/resources/yelp/test.csv"));
    String[] header = csvReader.readNext() ;
    String[] rowCell = null ;
    HashMap<String, String> record = new HashMap<String, String>() ;
    while(((rowCell = csvReader.readNext()) != null)) {
    }
  }
}
