package org.saarus.knime.nlp.text.learner;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "HadoopNode" Node. Hadoop plugin for Knime
 * @author Tuan Nguyen
 */
public class TextLearnerNodeFactory extends NodeFactory<TextLearnerNodeModel> {

  /** {@inheritDoc} */
  @Override
  public TextLearnerNodeModel createNodeModel() {
    return new TextLearnerNodeModel();
  }

  /** {@inheritDoc} */
  @Override
  public int getNrNodeViews() { return 1; }

  /** {@inheritDoc} */
  @Override
  public NodeView<TextLearnerNodeModel> createNodeView(final int viewIndex, final TextLearnerNodeModel nodeModel) {
    return new TextLearnerNodeView(nodeModel);
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasDialog() { return true; }

  /** {@inheritDoc} */
  @Override
  public NodeDialogPane createNodeDialogPane() {
    //return new FileImportNodeDialog();
    return new TextLearnerNodeDialog();
  }
}