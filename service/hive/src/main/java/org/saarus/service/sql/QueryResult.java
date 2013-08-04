package org.saarus.service.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import org.saarus.service.util.TabularPrinter;

public class QueryResult {
  private String     query ;
  private String[]   column ;
  private Object[][] data ;
  
  public QueryResult() { }
  
  public QueryResult(String query, ResultSet res) throws Exception {
    ResultSetMetaData rsmd = res.getMetaData() ;
    int columnCount = rsmd.getColumnCount() ;
    String[] columnNames = new String[columnCount];
    for(int i = 0; i < columnCount; i++) {
      columnNames[i] = rsmd.getColumnName(i + 1) ;
    }
    List<Object[]> rows = new ArrayList<Object[]>() ;
    while(res.next()) {
      Object[] row = new Object[columnCount] ;
      for(int i = 0; i < columnCount; i++) {
        row[i] = res.getObject(i + 1) ;
      }
      rows.add(row) ;
    }
    res.close() ;
    setQuery(query) ;
    setColumn(columnNames) ;
    Object[][] data = rows.toArray(new Object[rows.size()][]) ;
    setData(data) ;
  }
  
  public String getQuery() { return query; }
  public void   setQuery(String query) { this.query = query; }
  
  public String[] getColumn() { return column; }
  public void setColumn(String[] column) { this.column = column; }
  
  public Object[][] getData() { return data; }
  public void setData(Object[][] data) { this.data = data; }
  
  public void dump() {
    int[] width = new int[column.length] ;
    for(int i = 0; i < width.length; i++) {
      width[i] = 20 ;
    }
    dump(width) ;
  }
  
  public void dump(int ... width) {
    TabularPrinter p = new TabularPrinter(System.out, width) ;
    p.printHeader(column) ;
    for(int i = 0; i < data.length; i++) {
      p.printRow(data[i]) ;
    }
  }

  public String toString() {
    StringBuilder b = new StringBuilder() ;
    b.append(query).append("(Found " + data.length + " records)") ;
    return b.toString() ;
  }
}
