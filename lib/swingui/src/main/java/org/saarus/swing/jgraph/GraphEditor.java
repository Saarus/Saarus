package org.saarus.swing.jgraph;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.util.mxResources;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.util.mxUndoableEdit.mxUndoableChange;
import com.mxgraph.view.mxGraph;

public class GraphEditor extends JPanel {
  private static final long serialVersionUID = -6561623072112577140L;
  /** Adds required resources for i18n */
  static {
    try {
      mxResources.add("editor");
    } catch (Exception e) {
      e.printStackTrace() ;
    }
  }

  private GraphEditorToolBar toolbar ;
  protected mxGraphComponent graphComponent;
  protected mxGraphOutline graphOutline;

  protected JTabbedPane libraryPane;
  protected mxUndoManager undoManager;

  protected String appTitle;
  protected JLabel statusBar;

  protected boolean modified = false;

  protected mxRubberband rubberband;
  
  protected mxIEventListener undoHandler = new mxIEventListener() {
    public void invoke(Object source, mxEventObject evt) {
      undoManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));
    }
  };

  protected mxIEventListener changeTracker = new mxIEventListener() {
    public void invoke(Object source, mxEventObject evt) {
      setModified(true);
    }
  };

  public GraphEditor(String appTitle, mxGraphComponent component) {
    // Stores and updates the frame title
    this.appTitle = appTitle;

    // Stores a reference to the graph and creates the command history
    graphComponent = component;
    final mxGraph graph = graphComponent.getGraph();
    undoManager = createUndoManager();

    // Do not change the scale and translation after files have been loaded
    graph.setResetViewOnRootChange(false);

    // Updates the modified flag if the graph model changes
    graph.getModel().addListener(mxEvent.CHANGE, changeTracker);

    // Adds the command history to the model and view
    graph.getModel().addListener(mxEvent.UNDO, undoHandler);
    graph.getView().addListener(mxEvent.UNDO, undoHandler);

    // Keeps the selection in sync with the command history
    mxIEventListener undoHandler = new mxIEventListener() {
      public void invoke(Object source, mxEventObject evt) {
        List<mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
        graph.setSelectionCells(graph.getSelectionCellsForChanges(changes));
      }
    };

    undoManager.addListener(mxEvent.UNDO, undoHandler);
    undoManager.addListener(mxEvent.REDO, undoHandler);

    // Creates the graph outline component
    graphOutline = new mxGraphOutline(graphComponent);

    // Creates the library pane that contains the tabs with the palettes
    libraryPane = new JTabbedPane();

    // Creates the inner split pane that contains the library with the
    // palettes and the graph outline on the left side of the window
    JSplitPane inner = new JSplitPane(JSplitPane.VERTICAL_SPLIT, libraryPane, graphOutline);
    inner.setDividerLocation(320);
    inner.setResizeWeight(1);
    inner.setDividerSize(6);
    inner.setBorder(null);

    // Creates the outer split pane that contains the inner split pane and
    // the graph component on the right side of the window
    JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inner, graphComponent);
    outer.setOneTouchExpandable(true);
    outer.setDividerLocation(200);
    outer.setDividerSize(6);
    outer.setBorder(null);

    // Creates the status bar
    statusBar = createStatusBar();

    // Display some useful information about repaint events
    installRepaintListener();
    
    
    // Puts everything together
    setLayout(new BorderLayout());
    toolbar = new GraphEditorToolBar(this, JToolBar.HORIZONTAL); 
    add(toolbar, BorderLayout.NORTH);
    add(outer, BorderLayout.CENTER);
    add(statusBar, BorderLayout.SOUTH);
    
    // Installs rubberband selection and handling for some special
    // keystrokes such as F2, Control-C, -V, X, A etc.
    installHandlers();
    installListeners();
  }

  protected mxUndoManager createUndoManager() { return new mxUndoManager(); }

  protected void installHandlers() {
    rubberband = new mxRubberband(graphComponent);
  }

  protected JLabel createStatusBar() {
    JLabel statusBar = new JLabel(mxResources.get("ready"));
    statusBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
    return statusBar;
  }

  protected void installRepaintListener() {
    graphComponent.getGraph().addListener(mxEvent.REPAINT, new mxIEventListener() {
      public void invoke(Object source, mxEventObject evt) {
        String buffer = (graphComponent.getTripleBuffer() != null) ? "" : " (unbuffered)";
        mxRectangle dirty = (mxRectangle) evt.getProperty("region");

        if (dirty == null) {
          status("Repaint all" + buffer);
        } else {
          status("Repaint: x=" + (int) (dirty.getX()) + " y="
                 + (int) (dirty.getY()) + " w="
                 + (int) (dirty.getWidth()) + " h="
                 + (int) (dirty.getHeight()) + buffer);
        }
      }
    });
  }

  public GraphEditorToolBar getToolbar() { return this.toolbar ; }
  
  public GraphEditorPalette insertPalette(String title) {
    final GraphEditorPalette palette = new GraphEditorPalette();
    final JScrollPane scrollPane = new JScrollPane(palette);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    libraryPane.add(title, scrollPane);
    // Updates the widths of the palettes if the container size changes
    libraryPane.addComponentListener(new ComponentAdapter() {
      public void componentResized(ComponentEvent e) {
        int w = scrollPane.getWidth() - scrollPane.getVerticalScrollBar().getWidth();
        palette.setPreferredWidth(w);
      }
    });
    return palette;
  }

  protected void mouseWheelMoved(MouseWheelEvent e) {
    if (e.getWheelRotation() < 0) {
      graphComponent.zoomIn();
    } else {
      graphComponent.zoomOut();
    }
    status(mxResources.get("scale") + ": " + (int) (100 * graphComponent.getGraph().getView().getScale()) + "%");
  }

  protected void showOutlinePopupMenu(MouseEvent e) {
    Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), graphComponent);
    JCheckBoxMenuItem item = new JCheckBoxMenuItem(mxResources.get("magnifyPage"));
    item.setSelected(graphOutline.isFitPage());
    item.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        graphOutline.setFitPage(!graphOutline.isFitPage());
        graphOutline.repaint();
      }
    });

    JCheckBoxMenuItem item2 = new JCheckBoxMenuItem(mxResources.get("showLabels"));
    item2.setSelected(graphOutline.isDrawLabels());
    item2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        graphOutline.setDrawLabels(!graphOutline.isDrawLabels());
        graphOutline.repaint();
      }
    });

    JCheckBoxMenuItem item3 = new JCheckBoxMenuItem(mxResources.get("buffering"));
    item3.setSelected(graphOutline.isTripleBuffered());

    item3.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        graphOutline.setTripleBuffered(!graphOutline.isTripleBuffered());
        graphOutline.repaint();
      }
    });

    JPopupMenu menu = new JPopupMenu();
    menu.add(item);
    menu.add(item2);
    menu.add(item3);
    menu.show(graphComponent, pt.x, pt.y);
    e.consume();
  }

  protected void showGraphPopupMenu(MouseEvent e) {
    Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), graphComponent);
    GraphEditorPopupMenu popupMenu = new GraphEditorPopupMenu(GraphEditor.this);
    popupMenu.addDefault(GraphEditor.this) ;
    popupMenu.show(graphComponent, pt.x, pt.y);
    e.consume();
  }

  protected void mouseLocationChanged(MouseEvent e) {
    status(e.getX() + ", " + e.getY());
  }

  protected void installListeners() {
    // Installs mouse wheel listener for zooming
    MouseWheelListener wheelTracker = new MouseWheelListener() {
      public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getSource() instanceof mxGraphOutline || e.isControlDown()) {
          GraphEditor.this.mouseWheelMoved(e);
        }
      }
    };

    // Handles mouse wheel events in the outline and graph component
    graphOutline.addMouseWheelListener(wheelTracker);
    graphComponent.addMouseWheelListener(wheelTracker);

    // Installs the popup menu in the outline
    graphOutline.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        mouseReleased(e);
      }

      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          showOutlinePopupMenu(e);
        }
      }

    });

    // Installs the popup menu in the graph component
    graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        // Handles context menu on the Mac where the trigger is on mousepressed
        mouseReleased(e);
      }

      public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
          showGraphPopupMenu(e);
        }
      }
    });

    // Installs a mouse motion listener to display the mouse location
    graphComponent.getGraphControl().addMouseMotionListener(
      new MouseMotionListener() {
        public void mouseDragged(MouseEvent e) {
          mouseLocationChanged(e);
        }

        public void mouseMoved(MouseEvent e) {
          mouseDragged(e);
        }
    });
  }

  public void setModified(boolean modified) {
    boolean oldValue = this.modified;
    this.modified = modified;
    firePropertyChange("modified", oldValue, modified);
  }

  /** @return whether or not the current graph has been modified */
  public boolean isModified() { return modified; }

  public mxGraphComponent getGraphComponent() { return graphComponent; }

  public mxGraphOutline getGraphOutline() { return graphOutline; }

  public JTabbedPane getLibraryPane() { return libraryPane; }

  public mxUndoManager getUndoManager() { return undoManager; }

  /**
   * @param name
   * @param action
   * @return a new Action bound to the specified string name
   */
  public Action bind(String name, final Action action) { return bind(name, action, null) ; }

  /**
   * @param name
   * @param action
   * @return a new Action bound to the specified string name and icon
   */
  @SuppressWarnings("serial")
  public Action bind(String name, final Action action, String iconUrl) {
    AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(GraphEditor.class.getResource(iconUrl)) : null) {
      public void actionPerformed(ActionEvent e) {
        action.actionPerformed(new ActionEvent(getGraphComponent(), e.getID(), e.getActionCommand()));
      }
    };
    newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));
    return newAction;
  }

  public void status(String msg) { statusBar.setText(msg); }
}