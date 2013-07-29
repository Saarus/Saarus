package org.saarus.knime.data.hive;

import org.knime.core.node.NodeView;

/**
 * @author Tuan Nguyen
 */
public class QueryNodeView extends NodeView<QueryNodeModel> {

  /**
   * @param nodeModel The model (class: {@link QueryNodeModel})
   */
  protected QueryNodeView(final QueryNodeModel nodeModel) {
    super(nodeModel);
  }

  @Override
  protected void modelChanged() {
    QueryNodeModel nodeModel = (QueryNodeModel)getNodeModel();
    assert nodeModel != null;
  }

  @Override
  protected void onClose() {
  }

  @Override
  protected void onOpen() { }
}