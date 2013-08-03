package org.saarus.knime.data.hive;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "HadoopNode" Node. Hadoop plugin for Knime
 * @author Tuan Nguyen
 */
public class QueryNodeFactory extends NodeFactory<QueryNodeModel> {

  /** {@inheritDoc} */
  @Override
  public QueryNodeModel createNodeModel() {
    return new QueryNodeModel();
  }

  /** {@inheritDoc} */
  @Override
  public int getNrNodeViews() { return 1; }

  /** {@inheritDoc} */
  @Override
  public NodeView<QueryNodeModel> createNodeView(final int viewIndex, final QueryNodeModel nodeModel) {
    return new QueryNodeView(nodeModel);
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasDialog() { return true; }

  /** {@inheritDoc} */
  @Override
  public NodeDialogPane createNodeDialogPane() {
    //return new FileImportNodeDialog();
    return new QueryNodeDialog();
  }
}