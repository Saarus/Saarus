package org.saarus.swing.jgraph;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

public class GraphEditorActions {
  public static final GraphEditor getEditor(ActionEvent e) {
    if (e.getSource() instanceof Component) {
      Component component = (Component) e.getSource();
      while (component != null && !(component instanceof GraphEditor)) {
        component = component.getParent();
      }
      return (GraphEditor) component;
    }
    return null;
  }

  @SuppressWarnings("serial")
  public static class HistoryAction extends AbstractAction {
    protected boolean undo;
    
    public HistoryAction(boolean undo) {
      this.undo = undo;
    }

    public void actionPerformed(ActionEvent e) {
      GraphEditor editor = getEditor(e);
      if (editor != null) {
        if (undo) {
          editor.getUndoManager().undo();
        } else {
          editor.getUndoManager().redo();
        }
      }
    }
  }
  
  public static final mxGraph getGraph(ActionEvent e) {
    Object source = e.getSource();
    if (source instanceof mxGraphComponent) {
      return ((mxGraphComponent) source).getGraph();
    }
    return null;
  }
}
