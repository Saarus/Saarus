package org.saarus.knime.data.io.file;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "HadoopNode" Node. Hadoop plugin for Knime
 * @author Tuan Nguyen
 */
public class FileImportNodeFactory extends NodeFactory<FileImportNodeModel> {

  /** {@inheritDoc} */
  @Override
  public FileImportNodeModel createNodeModel() { 
    return new FileImportNodeModel(); 
  }

  /** {@inheritDoc} */
  @Override
  public int getNrNodeViews() { return 1; }

  /** {@inheritDoc} */
  @Override
  public NodeView<FileImportNodeModel> createNodeView(final int viewIndex, final FileImportNodeModel nodeModel) {
    return new FileImportNodeView(nodeModel);
  }

  /** {@inheritDoc} */
  @Override
  public boolean hasDialog() { return true; }

  /** {@inheritDoc} */
  @Override
  public NodeDialogPane createNodeDialogPane() { 
    return new FileImportNodeDialog(); 
  }
}