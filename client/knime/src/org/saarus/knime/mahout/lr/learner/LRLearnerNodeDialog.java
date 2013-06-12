package org.saarus.knime.mahout.lr.learner;

import java.awt.Dimension;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.saarus.knime.data.stat.StatisticConfigs;

/**
 * @author Tuan Nguyen
 */
public class LRLearnerNodeDialog extends NodeDialogPane {
  final static public int WIDTH  = 750 ;
  final static public int HEIGHT = 500 ;
  
  protected LRLearnerNodeDialog() {
    getPanel().setPreferredSize(new Dimension(WIDTH, HEIGHT)) ;

    addTab("Mahout Logistic Regression Learner", new LRLearnerJPanel());
  }

  protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs) throws NotConfigurableException {
    try {
      LRLearnerSettings lrSettings = new LRLearnerSettings(settings) ;
      System.out.println("dialog load settings from " );
      System.out.println(lrSettings);
    } catch (InvalidSettingsException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
    System.out.println("dialog saveSettingsTo..................");
    StatisticConfigs statSettings = new StatisticConfigs() ;
    String name = "table " + Math.random() ;
    statSettings.add(name, "desc", "path") ;
    statSettings.saveSettings(settings) ;
    System.out.println("dialog saveSettingsTo.................. done");
  }
  
  public void onClose() {
    System.out.println("-----------------------close diaglog-------------------------") ;
  }

  public void onOpen() {
    System.out.println("-----------------------open dialog--------------------------") ;
  }
}