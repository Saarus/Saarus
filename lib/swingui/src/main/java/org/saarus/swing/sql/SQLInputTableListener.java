package org.saarus.swing.sql;

public interface SQLInputTableListener {
  public void onSelectInputTableField(SQLTable table, SQLTable.Field field, boolean select) ;
  public void onAddInputTable(SQLTable table) ;
  public void onRemoveInputTable(SQLTable table) ;
}