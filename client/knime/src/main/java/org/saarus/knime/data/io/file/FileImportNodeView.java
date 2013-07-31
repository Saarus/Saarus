package org.saarus.knime.data.io.file;

import org.knime.core.node.NodeView;

/**
 * <code>NodeView</code> for the "HadoopNode" Node.Hadoop plugin for Knime
 * @author Tuan Nguyen
 */
public class FileImportNodeView extends NodeView<FileImportNodeModel> {

  /**
   * Creates a new view.
   * @param nodeModel The model (class: {@link FileImportNodeModel})
   */
  protected FileImportNodeView(final FileImportNodeModel nodeModel) {
    super(nodeModel);
  }

  @Override
  protected void modelChanged() {
    // TODO retrieve the new model from your nodemodel and 
    // update the view.
    FileImportNodeModel nodeModel = (FileImportNodeModel)getNodeModel();
    assert nodeModel != null;
    // be aware of a possibly not executed nodeModel! The data you retrieve
    // from your nodemodel could be null, emtpy, or invalid in any kind.
  }

  @Override
  protected void onClose() { }

  @Override
  protected void onOpen() {  }
}