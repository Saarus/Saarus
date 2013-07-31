package org.saarus.knime.data.stat;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "HadoopNode" Node. Hadoop plugin for Knime
 * @author Tuan Nguyen
 */
public class StatisticNodeFactory extends NodeFactory<StatisticNodeModel> {

  /** {@inheritDoc} */
  @Override
  public StatisticNodeModel createNodeModel() {
    return new StatisticNodeModel();
  }

  /** {@inheritDoc} */
  @Override
  public int getNrNodeViews() { return 1; }

  /** {@inheritDoc} */
  @Override
  public NodeView<StatisticNodeModel> createNodeView(final int viewIndex, final StatisticNodeModel nodeModel) {
    return new StatisticNodeView(nodeModel);
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasDialog() { return true; }

  /** {@inheritDoc} */
  @Override
  public NodeDialogPane createNodeDialogPane() {
    //return new FileImportNodeDialog();
    return new StatisticNodeDialog();
  }
}