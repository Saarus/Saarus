package org.saarus.knime.data.in.json;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "HadoopNode" Node. Hadoop plugin for Knime
 * @author Tuan Nguyen
 */
public class JSONImportNodeFactory extends NodeFactory<JSONImportNodeModel> {

  /** {@inheritDoc} */
  @Override
  public JSONImportNodeModel createNodeModel() { return new JSONImportNodeModel(); }

  /** {@inheritDoc} */
  @Override
  public int getNrNodeViews() { return 1; }

  /** {@inheritDoc} */
  @Override
  public NodeView<JSONImportNodeModel> createNodeView(final int viewIndex, final JSONImportNodeModel nodeModel) {
    return new JSONImportNodeView(nodeModel);
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasDialog() { return true; }

  /** {@inheritDoc} */
  @Override
  public NodeDialogPane createNodeDialogPane() { 
    return new JSONImportNodeDialog(); 
  }
}