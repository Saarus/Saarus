package org.knime.example;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "NumericBinner" Node.
 * Simple numeric binner with equidistant bins.
 *
 * @author KNIME.com AG, Zurich, Switzerland
 */
public class NumericBinnerNodeFactory 
extends NodeFactory<NumericBinnerNodeModel> {

  /** {@inheritDoc} */
  @Override
  public NumericBinnerNodeModel createNodeModel() {
    return new NumericBinnerNodeModel();
  }

  /** {@inheritDoc} */
  @Override
  public int getNrNodeViews() {
    return 1;
  }

  /** {@inheritDoc} */
  @Override
  public NodeView<NumericBinnerNodeModel> createNodeView(final int viewIndex,
      final NumericBinnerNodeModel nodeModel) {
    return new NumericBinnerNodeView(nodeModel);
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasDialog() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public NodeDialogPane createNodeDialogPane() {
    return new NumericBinnerNodeDialog();
  }

}
