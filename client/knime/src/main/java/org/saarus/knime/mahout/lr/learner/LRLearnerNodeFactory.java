package org.saarus.knime.mahout.lr.learner;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "HadoopNode" Node. Hadoop plugin for Knime
 * @author Tuan Nguyen
 */
public class LRLearnerNodeFactory extends NodeFactory<LRLearnerNodeModel> {

  /** {@inheritDoc} */
  @Override
  public LRLearnerNodeModel createNodeModel() {
    return new LRLearnerNodeModel();
  }

  /** {@inheritDoc} */
  @Override
  public int getNrNodeViews() { return 1; }

  /** {@inheritDoc} */
  @Override
  public NodeView<LRLearnerNodeModel> createNodeView(final int viewIndex, final LRLearnerNodeModel nodeModel) {
    return new LRLearnerNodeView(nodeModel);
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasDialog() { return true; }

  /** {@inheritDoc} */
  @Override
  public NodeDialogPane createNodeDialogPane() {
    //return new FileImportNodeDialog();
    return new LRLearnerNodeDialog();
  }
}