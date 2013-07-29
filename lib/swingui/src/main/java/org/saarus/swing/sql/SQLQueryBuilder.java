package org.saarus.swing.sql;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import org.saarus.swing.jgraph.GraphComponent;
import org.saarus.swing.jgraph.GraphEditor;
import org.saarus.swing.jgraph.GraphEditorPalette;
import org.saarus.swing.jgraph.GraphEditorPopupMenu;
import org.saarus.swing.jgraph.GraphVertex;
import org.saarus.swing.util.SwingUtil;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxGraphTransferHandler;
import com.mxgraph.swing.util.mxGraphTransferable;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;

public class SQLQueryBuilder extends GraphEditor {
  static String EDGE_ATTR = "edgeStyle=elbowEdgeStyle;elbow=horizontal" ; 
  
  GraphEditorPalette sourcePallete  ;
  GraphEditorPalette outputPallete ;
  
  public SQLQueryBuilder() {
    super("SQL Query Builder", new SQLQueryBuilderGraphComponent());

    getGraphComponent().setVisible(true);
    getGraphComponent().setGridVisible(true) ;
    getGraphComponent().setZoomPolicy(mxGraphComponent.ZOOM_POLICY_WIDTH);

    getGraphComponent().getGraph().setCellsResizable(false);
    getGraphComponent().setConnectable(false);
    getGraphComponent().getGraphHandler().setCloneEnabled(false);
    getGraphComponent().getGraphHandler().setImagePreview(false);
    
    sourcePallete = insertPalette("Sources");
    outputPallete = insertPalette("Output");
  }
  
  public void addSources(SQLTable ... tables) {
    ImageIcon tableIcon = new ImageIcon(getClass().getResource("/icons/rectangle.png")) ;
    for(SQLTable sel : tables) {
      mxCell tableTemplate = new mxCell(sel.getCellLabel(), new mxGeometry(0, 0,250, 150), null);
      tableTemplate.setValue(sel);
      tableTemplate.setVertex(true);
      sourcePallete.addTemplate(sel.getCellLabel(), tableIcon, tableTemplate);
    }
  }
  
  public void addOutput(SQLTable ... tables) {
    ImageIcon tableIcon = new ImageIcon(getClass().getResource("/icons/rectangle.png")) ;
    for(SQLTable sel : tables) {
      mxCell tableTemplate = new mxCell(sel.getCellLabel(), new mxGeometry(0, 0,300, 400), null);
      tableTemplate.setValue(sel);
      tableTemplate.setVertex(true);
      outputPallete.addTemplate(sel.getCellLabel(), tableIcon, tableTemplate);
    }
  }
    
  public void insertSample(SQLTable[] input, SQLTable output) {
    mxGraph graph = getGraphComponent().getGraph();
    Object parent = graph.getDefaultParent();
    graph.getModel().beginUpdate();
    try {
      mxCell[] inputCell = new mxCell[input.length] ;
      for(int i = 0; i < input.length; i++) {
        inputCell[i]  = (mxCell)graph.insertVertex(parent, null, input[i], 20, i * 250, 200, 200);
      }
      mxCell outputCell  = (mxCell)graph.insertVertex(parent, null, output, 400, 50, 300, 400);
      
      String edgeAttr = "edgeStyle=elbowEdgeStyle;elbow=horizontal" ;
      for(int i = 0; i < inputCell.length; i++) {
        graph.insertEdge(parent, null, "Edge", inputCell[i], outputCell, edgeAttr);
      }
    } finally {
      graph.getModel().endUpdate();
    }
  }

  public void broadcastSelectInputTableField(SQLTable table, SQLTable.Field field, boolean select) {
    List<SQLInputTableListener> listeners = 
        SwingUtil.findDescendantOfType(getGraphComponent(), SQLInputTableListener.class) ;
    for(SQLInputTableListener sel : listeners) {
      sel.onSelectInputTableField(table, field, select) ;
    }
  }
  
  public void onAddInputTable(mxCell cell, SQLTable table) {
    mxGraph graph = getGraphComponent().getGraph() ;
    Object[] unselectedCell = SQLQueryBuilderUtil.getUnselectedCells(this) ;
    mxCell[] inputCell = SQLQueryBuilderUtil.getSQLTableCell(unselectedCell, SQLTable.INPUT_TABLE_TYPE) ;
    double maxY = SQLQueryBuilderUtil.getCellMaxY(this, inputCell) ;
    mxRectangle rect = graph.getBoundingBox(cell) ;
    graph.resizeCell(cell, new mxRectangle(10, maxY + 10, rect.getWidth(), rect.getHeight())) ;
    
    mxCell outputCell = SQLQueryBuilderUtil.getSQLOutputTableCell(this) ;
    try {
      graph.getModel().beginUpdate();
      Object parent = graph.getDefaultParent();
      graph.insertEdge(parent, null, "", cell, outputCell, EDGE_ATTR);
    } finally {
      graph.getModel().endUpdate();
    }
    List<SQLInputTableListener> listeners = 
        SwingUtil.findDescendantOfType(getGraphComponent(), SQLInputTableListener.class) ;
    for(SQLInputTableListener sel : listeners) {
      sel.onAddInputTable(table) ;
    }
  }

  public void onRemoveInputTable(mxCell cell, SQLTable table) {
    List<SQLInputTableListener> listeners = 
        SwingUtil.findDescendantOfType(getGraphComponent(), SQLInputTableListener.class) ;
    for(SQLInputTableListener sel : listeners) {
      sel.onRemoveInputTable(table) ;
    }
  }

  public void onAddOutputTable(mxCell outputCell, SQLOutputTable outTable, SQLTable table) {
    mxGraph graph = getGraphComponent().getGraph() ;
    mxRectangle rect = graph.getBoundingBox(outputCell) ;
    graph.resizeCell(outputCell, new mxRectangle(350, 50, rect.getWidth(), rect.getHeight())) ;
    try {
      graph.getModel().beginUpdate();
      Object[] allCell = SQLQueryBuilderUtil.getAllCells(this) ;
      mxCell[] inputCells = SQLQueryBuilderUtil.getSQLTableCell(allCell, SQLTable.INPUT_TABLE_TYPE) ;
      Object parent = graph.getDefaultParent();
      for(mxCell inputCell : inputCells) {
        SQLTable sqlInputTable = (SQLTable)inputCell.getValue() ;
        outTable.onAddInputTable(sqlInputTable) ;
        graph.insertEdge(parent, null, "", inputCell, outputCell, EDGE_ATTR);
      }
    } finally {
      graph.getModel().endUpdate();
    }
    
  }
  
  public void onRemoveOutputTable(mxCell cell, SQLTable table) {
  }

  
  protected void showGraphPopupMenu(MouseEvent e) {
    Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), graphComponent);
    GraphEditorPopupMenu popupMenu = new GraphEditorPopupMenu(this);
    popupMenu.add(this, "delete", new SQLQueryBuilderActions.DeleteAction("Delete") , "/icons/delete.gif");
    popupMenu.show(graphComponent, pt.x, pt.y);
    e.consume();
  }
  
  static class SQLQueryBuilderGraphComponent extends GraphComponent {
    public SQLQueryBuilderGraphComponent() {
      super(new mxGraph() {
        public boolean isCellFoldable(Object cell, boolean collapse) {
          return model.isVertex(cell);
        }
      });
    }
    
    protected TransferHandler createTransferHandler() {
      return new mxGraphTransferHandler() {
        boolean canAddSQLTable(SQLQueryBuilder builder, Transferable t) {
          Object[] cell ;
          try {
            mxGraphTransferable mxtransferable = (mxGraphTransferable) t.getTransferData(mxGraphTransferable.dataFlavor);
            cell = mxtransferable.getCells() ;
            for(int i = 0; i < cell.length; i++) {
              Object model = ((mxCell)cell[i]).getValue() ;
              if(model instanceof SQLTable) {
                SQLTable table = (SQLTable) model ;
                if(SQLQueryBuilderUtil.getSQLTableCell(builder, table.getTableName()) != null) return false ;
                if(SQLTable.OUTPUT_TABLE_TYPE.equals(table.getType())) {
                  if(SQLQueryBuilderUtil.getSQLOutputTableCell(builder) != null) return false ;
                }
              }
            }
            return true ;
          } catch (Throwable e) {
            e.printStackTrace();
          }
          return false ;
        }
        
        public boolean importData(JComponent comp, Transferable t) {
          if(isLocalDrag()) return super.importData(comp, t) ;
          final SQLQueryBuilder builder = SQLQueryBuilderUtil.getSQLQueryBuilder(SQLQueryBuilderGraphComponent.this);
          if(!canAddSQLTable(builder, t)) return false ;
          boolean importRet = super.importData(comp, t) ;
          for(Object selCell : graph.getSelectionCells()) {
            mxCell mxCell = (mxCell) selCell ;
            GraphVertex gvertex = SQLQueryBuilderUtil.getGraphVertexByCell(builder, mxCell) ;
            SQLTable sqlTable = (SQLTable) mxCell.getValue() ;
            if(SQLTable.INPUT_TABLE_TYPE.equals(sqlTable.getType())) {
              builder.onAddInputTable(mxCell, sqlTable) ;
            } else {
              builder.onAddOutputTable(mxCell, (SQLOutputTable) gvertex.getVertexComponent(), sqlTable) ;
            }
          }
          return importRet ;
        }
      };
    }

    public Component[] createComponents(mxCellState state) {
      if (getGraph().getModel().isVertex(state.getCell())) {
        mxCell cell = (mxCell) state.getCell() ;
        SQLTable sqlTable = (SQLTable) cell.getValue() ;
        if(SQLTable.OUTPUT_TABLE_TYPE.equals(sqlTable.getType())) {
          return new Component[] { new GraphVertex(cell, this).setVertexContent(new SQLOutputTable(sqlTable), true) };
        } else {
          return new Component[] { new GraphVertex(cell, this).setVertexContent(new SQLInputTable(sqlTable), true) };
        }
      }
      return null;
    }
  }
}
