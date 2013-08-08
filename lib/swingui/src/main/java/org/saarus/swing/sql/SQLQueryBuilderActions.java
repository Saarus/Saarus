package org.saarus.swing.sql;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;

import org.saarus.swing.jgraph.GraphEditorActions;
import org.saarus.swing.sql.model.SQLTable;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

public class SQLQueryBuilderActions {
  public static class DeleteAction extends AbstractAction {
    public DeleteAction(String name) {
      super(name);
    }

    public void actionPerformed(ActionEvent e) {
      mxGraph graph = GraphEditorActions.getGraph(e);
      if (graph != null) {
        SQLQueryBuilder builder = SQLQueryBuilderUtil.getSQLQueryBuilder((JComponent) e.getSource()) ;
        Object[] selectionCell = graph.getSelectionCells() ;
        for(int i = 0; i < selectionCell.length; i++) {
          mxCell cell = (mxCell) selectionCell[i] ;
          Object value  = cell.getValue() ;
          if(value instanceof SQLTable) {
            SQLTable table = (SQLTable) value ;
            if(SQLTable.INPUT_TABLE_TYPE.equals(table.getType())) {
              builder.onRemoveInputTable(cell, table) ;
            } else {
              builder.onRemoveOutputTable(cell, table) ;
            }
          }
        }
        graph.removeCells();
      }
    }
  }
}
