package org.saarus.knime.mahout.lr.predictor;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "HadoopNode" Node. Hadoop plugin for Knime
 * @author Tuan Nguyen
 */
public class LRPredictorNodeFactory extends NodeFactory<LRPredictorNodeModel> {

  /** {@inheritDoc} */
  @Override
  public LRPredictorNodeModel createNodeModel() {
    return new LRPredictorNodeModel();
  }

  /** {@inheritDoc} */
  @Override
  public int getNrNodeViews() { return 1; }

  /** {@inheritDoc} */
  @Override
  public NodeView<LRPredictorNodeModel> createNodeView(final int viewIndex, final LRPredictorNodeModel nodeModel) {
    return new LRPredictorNodeView(nodeModel);
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasDialog() { return true; }

  /** {@inheritDoc} */
  @Override
  public NodeDialogPane createNodeDialogPane() {
    //return new FileImportNodeDialog();
    return new LRPredictorNodeDialog();
  }
}