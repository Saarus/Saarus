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

  /**
   * {@inheritDoc}
   */
  @Override
  protected void modelChanged() {
    QueryNodeModel nodeModel = (QueryNodeModel)getNodeModel();
    assert nodeModel != null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void onClose() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void onOpen() { }
}