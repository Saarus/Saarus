package org.saarus.swing.sql;

import org.saarus.swing.BeanBindingJTable;
import org.saarus.swing.sql.model.SQLTable;
import org.saarus.swing.sql.model.SQLTable.Field;

public class SQLInputTable extends BeanBindingJTable<Field> {
  static String COLUMN_NAMES[] = {"Sel", "Field", "Type"};
  static String BEAN_PROPERTY[] = {"select", "name", "type"};
  
  private SQLTable sqlTable ;
  
  public SQLInputTable(SQLTable table) {
    sqlTable = table ;
    init(COLUMN_NAMES, BEAN_PROPERTY, table.getFields()) ;
    getColumnModel().getColumn(0).setPreferredWidth(15) ;
  }
  
  public SQLTable getSQLTable() { return this.sqlTable ; }

  protected Field newBean() { return sqlTable.newField() ; }

  protected boolean isBeanEditableAt(int row, int col) {
    if(col == 0) return true ;
    return false ;
  }
  
  public void onChangeBeanData(int row, Field bean, String property, Object val) {
    SQLQueryBuilder builder = SQLQueryBuilderUtil.getSQLQueryBuilder(SQLInputTable.this) ;
    builder.broadcastSelectInputTableField(sqlTable, bean, (Boolean) val) ;
  }
  
}
