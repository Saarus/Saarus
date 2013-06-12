package org.saarus.service.mongodb;

import java.io.BufferedReader;
import java.io.FileReader;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class JSONReader {
  private String file  ;
  private BufferedReader reader ;
  private String currentLine = null ;
  
  public JSONReader(String file) throws Exception {
    this.file = file ;
    this.reader = new BufferedReader(new FileReader(file));
  }
  
  public String getSource() { return file ; }
  
  public boolean hasNext() throws Exception {
    String line = null ;
    currentLine = null ;
    while((line = reader.readLine()) != null) {
      line = line.trim();
      if(line.length() > 0) {
        currentLine = line ;
        break ;
      }
    }
    return currentLine != null ;
  }
  
  public DBObject next() throws Exception {
    return ( DBObject ) JSON.parse( currentLine );
  }
  
  public void close() throws Exception {
    reader.close() ;
  }
  
  static public void main(String[] args) throws Exception {
    JSONReader reader = new JSONReader("d:/projects/saarus/yelp/yelp_training_set/yelp_training_set_user.json") ;
    while(reader.hasNext()) {
      DBObject obj = reader.next() ;
      System.out.println(JSON.serialize(obj)) ;
    }
  }
}
