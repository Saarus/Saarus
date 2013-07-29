package org.saarus.swing.sql;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.saarus.swing.jgraph.GraphEditor;
import org.saarus.swing.jgraph.GraphVertex;
import org.saarus.swing.util.SwingUtil;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraph;

public class SQLQueryBuilderUtil {
  static public SQLQueryBuilder getSQLQueryBuilder(JComponent comp) {
    if(comp instanceof SQLQueryBuilder) return (SQLQueryBuilder) comp ;
    SQLQueryBuilder builder = (SQLQueryBuilder)SwingUtilities.getAncestorOfClass(SQLQueryBuilder.class, comp) ;
    return builder ;
  }
  
  static public GraphVertex getGraphVertexByCell(SQLQueryBuilder builder, Object cell) {
    List<GraphVertex> holder =  SwingUtil.findDescendantOfType(builder.getGraphComponent(), GraphVertex.class) ;
    for(GraphVertex sel : holder) {
      if(sel.getMxCell() == cell) return sel ;
    }
    return null ;
  }
  
  static public SQLTable[] getSQLInputTables(JComponent comp) {
    SQLQueryBuilder editor = (SQLQueryBuilder) getSQLQueryBuilder(comp) ;
    List<SQLInputTable> holder =  SwingUtil.findDescendantOfType(editor, SQLInputTable.class) ;
    SQLTable[] table = new SQLTable[holder.size()] ;
    for(int i = 0; i < table.length; i++) {
      table[i] = holder.get(i).getSQLTable() ;
    }
    return table ;
  }
  
  static public String[] getSQLInputTableNames(JComponent comp) {
    SQLTable[] table = getSQLInputTables(comp) ;
    String[] name = new String[table.length] ;
    for(int i = 0; i < table.length; i++) {
      name[i] =  table[i].getTableName();
    }
    return name ;
  }
  
  static public SQLOutputTable getSQLOutputTable(JComponent comp) {
    SQLQueryBuilder editor = getSQLQueryBuilder(comp) ;
    List<SQLOutputTable> holder =  SwingUtil.findDescendantOfType(editor, SQLOutputTable.class) ;
    if(holder.size() == 0) return null ;
    else if(holder.size() == 1) return holder.get(0) ;
    else throw new RuntimeException("Expect only 0 or 1 output table") ;
  }
  
  static public Object[] getAllCells(JComponent comp) { 
    return getAllCells(getSQLQueryBuilder(comp)) ; 
  }
  
  static public Object[] getAllCells(GraphEditor editor) {
    return editor.getGraphComponent().getCells(new Rectangle(0, 0, 100000, 100000)) ;
  }
  
  static public Object[] getUnselectedCells(GraphEditor editor) {
    Object[] all = getAllCells(editor) ;
    Object[] selCells =  editor.getGraphComponent().getGraph().getSelectionCells() ;
    List<Object> holder = new ArrayList<Object>() ;
    for(Object cell : all) {
      boolean select = false ;
      for(Object selCell : selCells) {
        if(cell == selCell) {
          select = true ;
          break ;
        }
      }
      if(!select) holder.add(cell) ;
    }
    return holder.toArray(new Object[holder.size()]) ;
  }
  
  static public mxCell[] getSQLTableCell(Object[] cell, String type) {
    List<mxCell> holder = new ArrayList<mxCell>() ;
    for(Object sel : cell) {
      mxCell mxCell = (mxCell) sel ;
      if(mxCell.getValue() instanceof SQLTable) {
        SQLTable sqlTable = (SQLTable) mxCell.getValue() ;
        if(type.equals(sqlTable.getType())) {
          holder.add(mxCell) ;
        }
      }
    }
    return holder.toArray(new mxCell[holder.size()])  ;
  }
  
  static public mxCell getSQLTableCell(JComponent comp, String tableName) {
    Object[] cell = getAllCells(comp) ;
    for(Object sel : cell) {
      if(sel instanceof mxCell) {
        mxCell selCell = (mxCell) sel ;
        Object value = selCell.getValue() ;
        if(value instanceof SQLTable) {
          SQLTable sqlTable = (SQLTable) value ;
          if(tableName.equals(sqlTable.getTableName())) {
            return selCell ;
          }
        }
      }
    }
    return null ;
  }
  
  static public mxCell getSQLOutputTableCell(JComponent comp) {
    Object[] cell = getAllCells(comp) ;
    for(Object sel : cell) {
      if(sel instanceof mxCell) {
        mxCell selCell = (mxCell) sel ;
        Object value = selCell.getValue() ;
        if(value instanceof SQLTable) {
          SQLTable sqlTable = (SQLTable) value ;
          if(SQLTable.OUTPUT_TABLE_TYPE.equals(sqlTable.getType())) {
            return selCell ;
          }
        }
      }
    }
    return null ;
  }
  
  static public double getCellMaxY(GraphEditor editor, Object[] cell) {
    double retY = 0 ;
    mxGraph graph = editor.getGraphComponent().getGraph() ;
    for(Object sel : cell) {
      mxRectangle rect = graph.getBoundingBox(sel) ;
      double ypos = rect.getY() + rect.getHeight();
      if(retY < ypos) retY = ypos ;
    }
    return retY ;
  }
}