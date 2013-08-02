package org.saarus.nlp.classify.liblinear;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

public interface TextClassifyDataReader {
  
  public Record next() throws Exception ;
  
  public void close() throws Exception ;
  
  static public class Record {
    String label ;
    String text ;
    
    public Record(String label, String text) {
      this.label = label ;
      this.text = text ;
    }
    
    public String getText() { return this.text ; }
    
    public String getLabel() { return this.label; }
  }
  
  static public class CSVDataReader implements TextClassifyDataReader {
    CSVReader csvReader ;
    String[] header ;
    
    public CSVDataReader(String cvsFile) throws Exception {
      csvReader = new CSVReader(new FileReader(cvsFile));
      //read first line as the header
      header = csvReader.readNext() ;
    }
    
    public Record next() throws Exception {
      String[] rowCell = csvReader.readNext()  ;
      if(rowCell == null) return null ;
      return createRecord(rowCell);
    }
    
    public Map<String, String> nextRow() throws Exception {
      String[] rowCell = csvReader.readNext()  ;
      if(rowCell == null) return null ;
      Map<String, String> map = new HashMap<String, String>() ;
      for(int i = 0; i < header.length; i++) {
        map.put(header[i], rowCell[i]) ;
      }
      return map;
    }
    
    protected Record createRecord(String[] cell) {
      return new Record(cell[0], cell[1]) ;
    }
    
    public void close() throws Exception {
      csvReader.close() ;
    }
  }
}
