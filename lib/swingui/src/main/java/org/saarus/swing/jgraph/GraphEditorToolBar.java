package org.saarus.swing.jgraph;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JToolBar;

import org.saarus.swing.jgraph.GraphEditorActions.HistoryAction;

public class GraphEditorToolBar extends JToolBar {
  private static final long serialVersionUID = -8015443128436394471L;

  public GraphEditorToolBar(final GraphEditor editor, int orientation) {
    super(orientation);
    setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3), getBorder()));
    setFloatable(false);

    add(editor.bind("Undo", new HistoryAction(true),  "/icons/undo.gif"));
    add(editor.bind("Redo", new HistoryAction(false), "/icons/redo.gif"));
    addSeparator();
  }
  
  public void add(GraphEditor editor, String name, String icon, Action action) {
    add(editor.bind(name, action, icon));
  }
}
