package org.saarus.swing.sql;

import static org.saarus.swing.sql.SQLStringBuilder.padding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.saarus.swing.jgraph.GraphCellModel;

public class SQLTable implements GraphCellModel, Serializable {
  final static public String INPUT_TABLE_TYPE = "input" ;
  final static public String OUTPUT_TABLE_TYPE = "output" ;
  
  private String cellLabel ;
  private String type ;
  private String tableName ;
  private List<Field> fields = new ArrayList<Field>() ;
  
  public SQLTable() {}
  
  public SQLTable(String name) {
    this.tableName = name ;
  }
  
  public String getCellLabel() { 
    if(cellLabel == null) return this.tableName ; 
    return cellLabel ;
  }
  
  public void setCellLabel(String s) { this.cellLabel = s ;}
  
  public String getType() { return this.type ; }
  public void   setType(String type) { this.type = type ; }
  
  public String getTableName() { return tableName; }
  public void setTableName(String tableName) { this.tableName = tableName; }

  public List<Field> getFields() { return fields; }
  public void setFields(List<Field> fields) { this.fields = fields; }
  
  public Field getField(int idx) { return fields.get(idx) ; }
  
  public Field addField(String name, String type, String comment) {
    Field field = new Field(name, type, comment) ;
    fields.add(field) ;
    return field ;
  }
  
  public Field addField(Field fromField) {
    Field field = new Field(fromField.getName(), fromField.getType(), fromField.getComment()) ;
    field.setMapFrom(fromField) ;
    fields.add(field) ;
    return field ;
  }
  
  public void removeFieldByMapFrom(Field mapFrom) {
    Iterator<Field> i = fields.iterator() ;
    while(i.hasNext()) {
      Field sel = i.next() ;
      if(sel.mapFrom == mapFrom) {
        i.remove() ;
        break ;
      }
    }
  }
  
  public void removeFieldByMapFrom(SQLTable table) {
    for(Field sel : table.getFields()) {
      removeFieldByMapFrom(sel) ;
    }
  }

  public String buildDropTableSQL() {
    return "DROP TABLE IF EXISTS " + getTableName() ; 
  }
  
  public String buildCreateTable() {
    SQLStringBuilder out = new SQLStringBuilder() ;
    out.println("CREATE TABLE ", getTableName(), "(") ;
    for(int i = 0; i < fields.size(); i++) {
      Field field = fields.get(i) ;
      String comment = field.getComment() ;
      if(comment == null) comment = "'No comment'" ;
      else comment =  "'" + comment + "'" ;
      String eol = "," ;
      if(i == fields.size() - 1) eol = "" ;
      out.println("  ", padding(field.getName(), 30), padding(field.getType(), 10), "COMMENT", comment, eol);
    }
    out.print(")", "STORED AS RCFILE") ;
    String ret = out.toString() ;
    return ret; 
  }
  
  public String toString() { return getCellLabel() ; }
  
  public class Field implements Serializable {
    private boolean select ;
    private String  name; 
    private String  type ;
    private String  comment ;
    private String  expression ;
    private Field   mapFrom ;
    
    public Field() {}
    
    public Field(String name, String type, String comment) {
      this.name = name ;
      this.type = type ;
      this.comment = comment ;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isSelect() { return select ; }
    public void    setSelect(boolean b) { this.select = b; }
    
    public String getComment() { return this.comment ; }
    public void   setComment(String s) { this.comment = s ; }
    
    public String getExpression() { return expression; }
    public void setExpression(String transformExpression) {
      this.expression = transformExpression;
    }
    
    public Field   getMapFrom() { return this.mapFrom ; }
    public void    setMapFrom(Field from) { this.mapFrom = from ; }
    
    public String getMapFromExpression() {
      if(mapFrom == null) return "" ;
      return mapFrom.toString() ;
    }
    
    public String toString() {
      return getTableName() + "." + name ;
    }
  }
}
