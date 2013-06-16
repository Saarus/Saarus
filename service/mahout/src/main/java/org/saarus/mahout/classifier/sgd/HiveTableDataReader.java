package org.saarus.mahout.classifier.sgd;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.saarus.service.hive.HiveService;

public class HiveTableDataReader implements DataReader {
  private HiveService hservice;
  private String   table ;
  private String[] field ;
  private ResultSet resultSet ;
  
  public HiveTableDataReader(HiveService hservice, String table, String[] field) throws Exception {
    this.hservice = hservice ;
    this.table = table ;
    this.field = field ;
  }
  
  public List<String> getHeaderNames() { return Arrays.asList(field); }
  
  public List<String> nextRow() throws Exception {
    if(resultSet == null) return null ;
    if(resultSet.next()) {
      List<String> holder = new ArrayList<String>() ;
      for(int i = 0; i < field.length; i++) {
        holder.add(resultSet.getString(field[i])) ;
      }
      return holder;
    } else {
      resultSet.close() ;
      resultSet = null ;
      return null;
    }
  }

  public void reset() throws Exception {
    StringBuilder b = new StringBuilder() ;
    b.append("SELECT * ") ;
    if(field == null) {
      b.append(" * ") ;
    } else {
      for(int i = 0; i < field.length; i++) {
        if(i > 0) b.append(", ") ;
        b.append(field[i]) ;
      }
    }
    b.append(" FROM ").append(table) ;
    resultSet = hservice.executeQuerySQL(b.toString()) ;
    resultSet.setFetchSize(5000) ;
    if(field == null) {
      ResultSetMetaData meta = resultSet.getMetaData() ;
      int columnCount = meta.getColumnCount() ;
      field = new String[columnCount] ;
      for(int i = 0; i < columnCount ; i++) {
        field[i] = meta.getColumnName(i + 1) ;
      }
    }
  }
  
  public void close() throws Exception {
  }
  
}
