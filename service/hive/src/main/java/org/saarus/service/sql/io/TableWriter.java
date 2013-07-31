package org.saarus.service.sql.io;

public interface TableWriter {
  public void writeRow(String[] val) throws Exception ;
  public void close() throws Exception ;
}