package org.saarus.swing.jgraph;

import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.TransferHandler;

import com.mxgraph.util.mxResources;

public class GraphEditorPopupMenu extends JPopupMenu {
  public GraphEditorPopupMenu(GraphEditor editor) {
//    add(editor.bind(mxResources.get("delete"), new DeleteAction("Delete") , "/icons/delete.gif"));
  }
  
  public void addDefault(GraphEditor editor) {
    boolean selected = !editor.getGraphComponent().getGraph().isSelectionEmpty();
    add(editor.bind(mxResources.get("cut"), TransferHandler.getCutAction(),"/icons/cut.gif")).setEnabled(selected);
    add(editor.bind(mxResources.get("copy"), TransferHandler.getCopyAction(), "/icons/copy.gif")).setEnabled(selected);
    add(editor.bind(mxResources.get("paste"), TransferHandler.getPasteAction(), "/icons/paste.gif"));
  }
  
  public void add(GraphEditor editor, String name, Action action, String icon) {
    add(editor.bind(mxResources.get(name), action , icon));
  }
}