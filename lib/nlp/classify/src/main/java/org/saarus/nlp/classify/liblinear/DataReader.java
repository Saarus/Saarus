package org.saarus.nlp.classify.liblinear;

import java.io.FileNotFoundException;
import java.io.FileReader;

import au.com.bytecode.opencsv.CSVReader;

public interface DataReader {
  
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
  
  static public class CVSDataReader implements DataReader {
    CSVReader csvReader ;
    
    public CVSDataReader(String cvsFile) throws FileNotFoundException {
      csvReader = new CSVReader(new FileReader(cvsFile));
      
    }
    
    public Record next() throws Exception {
      String[] rowCell = csvReader.readNext()  ;
      if(rowCell == null) return null ;
      return createRecord(rowCell);
    }
    
    protected Record createRecord(String[] cell) {
      return new Record(cell[0], cell[1]) ;
    }
    
    public void close() throws Exception {
      csvReader.close() ;
    }
    
  }
}
