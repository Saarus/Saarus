package org.saarus.service.sql;

import java.util.ArrayList;
import java.util.List;

import org.saarus.service.util.StringUtil;

public class CacheDataReader implements DataReader {
  private List<String> headerNames ;
  private List<List<String>> rowDatas ; 
  private int currPos = 0;
  
  public CacheDataReader(DataReader reader) throws Exception {
    reader.reset() ;
    this.headerNames = reader.getHeaderNames() ;
    this.rowDatas = new ArrayList<List<String>>(100) ;
    List<String> rowData = null ;
    long start = System.currentTimeMillis() ;
    System.out.println("Start load cache data") ;
    int count = 0 ;
    System.out.println("Headers = " + StringUtil.joinStringCollection(headerNames, ", ")) ; 
    while((rowData = reader.nextRow()) != null) {
      rowDatas.add(rowData) ;
      count++ ;
    }
    System.out.println("Load " + count + " records in " + (System.currentTimeMillis() - start) + "ms") ;
  }
  
  public List<String> getHeaderNames() {return headerNames;}

  public List<String> nextRow() throws Exception {
    if(currPos < rowDatas.size()) {
      List<String> data = rowDatas.get(currPos) ;
      currPos++ ;
      return data ;
    }
    return null;
  }

  public void reset() throws Exception {
    currPos = 0 ;
  }

  public void close() throws Exception {
  }

}
