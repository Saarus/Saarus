package org.saarus.swing.sql;

import java.util.List;

import org.saarus.swing.sql.model.SQLTable;
import org.saarus.swing.sql.model.SQLTable.Field;

public class SQLStringBuilder {
  private StringBuilder out = new StringBuilder() ;
  
  public void print(String ... arg) {
    for(int i = 0; i < arg.length; i++) {
      if(i > 0) out.append(" ") ;
      out.append(arg[i]) ;
    }
  }
  
  public void println(String ... arg) {
    for(int i = 0; i < arg.length; i++) {
      if(i > 0) out.append(" ") ;
      out.append(arg[i]) ;
    }
    out.append("\n") ;
  }
  
  static public String padding(String cell, int width) {
    int len = cell.length() ;
    StringBuilder b = new StringBuilder() ;
    b.append(cell) ;
    for(int i = len; i < width; i++) {
      b.append(" ") ;
    }
    return b.toString(); 
  }

  public String buildDropTableSQL(SQLTable table) {
    out = new StringBuilder() ;
    return "DROP TABLE IF EXISTS " + table.getTableName() ; 
  }
  
  public String buildCreateTable(SQLTable table) {
    out = new StringBuilder() ;
    println("CREATE TABLE ", table.getTableName(), "(") ;
    List<Field> fields = table.getFields() ;
    for(int i = 0; i < fields.size(); i++) {
      Field field = fields.get(i) ;
      String comment = field.getComment() ;
      if(comment == null) comment = "'No comment'" ;
      else comment =  "'" + comment + "'" ;
      String eol = "," ;
      if(i == fields.size() - 1) eol = "" ;
      println("  ", padding(field.getName(), 20), padding(field.getType(), 10), "COMMENT", comment, eol);
    }
    println(") STORED AS RCFILE") ;
    String ret = out.toString() ;
    return ret; 
  }
  
  public String buildSelectQuery(SQLQuery sqlQuery) {
    out = new StringBuilder() ;
    println("SELECT") ;
    println("SELECT") ;
    String ret = out.toString() ;
    return ret; 
  }
  
  public String toString() { return out.toString() ; }
}
