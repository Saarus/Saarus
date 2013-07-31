package org.saarus.service.sql.io;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

public class CSVImporter {
  private TableWriter writer ;
  private Progressable progressable ;
  
  public CSVImporter(TableWriter writer, Progressable progressable) throws Exception {
    this.writer = writer ;
    this.progressable = progressable ;
  }
  
  public void doImport(InputStream is, String[] properties) throws Exception {
    CSVReader csvReader = new CSVReader(new InputStreamReader(is));
    //read first line as the header
    String[] header = csvReader.readNext() ;
    String[] rowCell = null ;
    while(((rowCell = csvReader.readNext()) != null)) {
      if(progressable != null) progressable.progress(properties, rowCell) ;
      writer.writeRow(getValues(header, properties, rowCell)) ;
    }
  }
  
  public void doImport(String file, String[] properties) throws Exception {
    doImport(new FileInputStream(file), properties) ;
  }
  
  public void close() throws Exception {
    writer.close() ;
  }
  
  private String[] getValues(String[] header, String[] properties, String[] values) {
    Map<String, String> record = new LinkedHashMap<String, String>() ;
    for(int i = 0; i < header.length; i++) {
      record.put(header[i], values[i]) ;
    }
    String[] pvalues = new String[properties.length] ;
    for(int i = 0; i < properties.length; i++) {
      pvalues[i] = record.get(properties[i]) ;
    }
    return pvalues ;
  } 
}