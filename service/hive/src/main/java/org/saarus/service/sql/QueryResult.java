package org.saarus.service.sql;

public class QueryResult {
  private String     query ;
  private String[]   column ;
  private Object[][] data ;
  
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
