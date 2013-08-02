package org.saarus.service.sql;

import java.util.List;

public interface DataReader {
  public List<String> getHeaderNames() ;
  public List<String> nextRow() throws Exception ;
  public void reset() throws Exception ;
  public void close() throws Exception ;
}
