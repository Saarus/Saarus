package org.saarus.swing.sql;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.saarus.swing.sql.SQLTable.Field;

public class SQLInputTable extends JTable {
  static String COLUMN_NAMES[] = {"Sel", "Field", "Type"};

  private SQLTable sqlTable ;
  private List<Field> fields = new ArrayList<Field>() ;
  
  public SQLInputTable(SQLTable table) {
    this.sqlTable = table ;
    setAutoscrolls(true) ;
    
    fields = table.getFields() ;
    final TableModel model = new AbstractTableModel() {
      public Class getColumnClass(int column) {
        switch (column) {
          case 0:  return Boolean.class;
          default: return String.class;
        }
      }
      
      public String getColumnName(int column) { return COLUMN_NAMES[column]; }
      
      public int getRowCount() { return fields.size() ;}
      public int getColumnCount() { return COLUMN_NAMES.length ; }

      public Object getValueAt(int rowIndex, int columnIndex) {
        if(columnIndex == 0) return fields.get(rowIndex).isSelect() ;
        else if(columnIndex == 1) return fields.get(rowIndex).getName() ;
        else if(columnIndex == 2) return fields.get(rowIndex).getType() ;
        return null;
      }
      
      public boolean isCellEditable(int rowIndex, int columnIndex) { return true ; }
      
      public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(columnIndex == 0) fields.get(rowIndex).setSelect((Boolean) aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
      }
    };
    setModel(model) ;
    getColumnModel().getColumn(0).setPreferredWidth(15) ;  
    
    model.addTableModelListener(new TableModelListener() {
      public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow() ;
        int col = e.getColumn() ;
        Object val = model.getValueAt(row, col) ;
        SQLQueryBuilder builder = SQLQueryBuilderUtil.getSQLQueryBuilder(SQLInputTable.this) ;
        String fieldName = (String) getModel().getValueAt(row, 1) ;
        SQLTable.Field field = sqlTable.getField(row) ;
        builder.broadcastSelectInputTableField(sqlTable, field, (Boolean) val) ;
      }
    });
  }
  
  public SQLTable getSQLTable() { return this.sqlTable ; }
}
