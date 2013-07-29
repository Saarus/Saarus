package org.saarus.swing.jgraph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxCellHandler;
import com.mxgraph.view.mxGraph;

public class GraphVertex extends JPanel {
  private static final long serialVersionUID = 2106746763664760745L;
  public static final String IMAGE_PATH = "/icons/";
  static ImageIcon REFERENCES_ICON = new ImageIcon(GraphVertex.class.getResource(IMAGE_PATH + "preferences.gif")) ;

  protected mxCell cell;
  protected mxGraphComponent graphContainer;
  protected mxGraph graph;
  public JComponent vertexContent;

  public GraphVertex(mxCell cell, mxGraphComponent graphContainer) {
    this.cell = cell;
    this.graphContainer = graphContainer;
    this.graph = graphContainer.getGraph();
    setLayout(new BorderLayout());
    setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

    JPanel title = new JPanel();
    title.setBackground(new Color(149, 173, 239));
    title.setOpaque(true);
    title.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 1));
    title.setLayout(new BorderLayout());

    JLabel icon = new JLabel(REFERENCES_ICON);
    icon.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 1));
    title.add(icon, BorderLayout.WEST);

    JLabel label = new JLabel(String.valueOf(graph.getLabel(cell)));
    label.setForeground(Color.WHITE);
    label.setFont(title.getFont().deriveFont(Font.BOLD, 11));
    label.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 2));
    title.add(label, BorderLayout.CENTER);
    
    add(title, BorderLayout.NORTH);

        
    label = new JLabel(new ImageIcon(GraphVertex.class.getResource(IMAGE_PATH + "resize.gif")));
    label.setCursor(new Cursor(Cursor.NW_RESIZE_CURSOR));

    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(label, BorderLayout.EAST);

    add(panel, BorderLayout.SOUTH);

    ResizeHandler resizeHandler = new ResizeHandler();
    label.addMouseListener(resizeHandler);
    label.addMouseMotionListener(resizeHandler);

    setMinimumSize(new Dimension(100, 50));
  }
  
  public mxCell getMxCell() { return this.cell ; } 
  
  public JComponent getVertexComponent() { return vertexContent ; } 
  
  public GraphVertex setVertexContent(JComponent comp, boolean scrollable) {
    this.vertexContent = comp ;
    JScrollPane scrollPane = new JScrollPane(comp);
    scrollPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    scrollPane.getViewport().setBackground(Color.WHITE);
    if (graph.getModel().getChildCount(cell) == 0) {
      setOpaque(true);
      add(scrollPane, BorderLayout.CENTER);
    }
    return this ;
  }

  
  public class ResizeHandler implements MouseListener, MouseMotionListener {
    //Default index is 7 (bottom right)
    protected int index ;

    public ResizeHandler() {
      this(7);
    }

    public ResizeHandler(int index) {
      this.index = index;
    }

    public void mouseClicked(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
      if (!graph.isCellSelected(cell)) {
        graphContainer.selectCellForEvent(cell, e);
      }

      // Initiates a resize event in the handler
      mxCellHandler handler = graphContainer.getSelectionCellsHandler().getHandler(cell);
      if (handler != null) {
        // Starts the resize at index 7 (bottom right)
        handler.start(SwingUtilities.convertMouseEvent((Component) e.getSource(), e, graphContainer.getGraphControl()), index);
        e.consume();
      }
    }

    public void mouseReleased(MouseEvent e) {
      graphContainer.getGraphControl().dispatchEvent(SwingUtilities.convertMouseEvent((Component) e.getSource(), e, graphContainer.getGraphControl()));
    }

    public void mouseDragged(MouseEvent e) {
      graphContainer.getGraphControl().dispatchEvent(SwingUtilities.convertMouseEvent((Component) e.getSource(), e, graphContainer.getGraphControl()));
    }

    public void mouseMoved(MouseEvent e) { }
  } 
}