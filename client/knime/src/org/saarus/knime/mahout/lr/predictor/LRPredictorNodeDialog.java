package org.saarus.knime.mahout.lr.predictor;

import java.awt.Dimension;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;

/**
 * @author Tuan Nguyen
 */
public class LRPredictorNodeDialog extends NodeDialogPane {
  final static public int WIDTH  = 750 ;
  final static public int HEIGHT = 500 ;
  
  protected LRPredictorNodeDialog() {
    getPanel().setPreferredSize(new Dimension(WIDTH, HEIGHT)) ;

    addTab("Mahout Logistic Regression Predictor", new LRPredictorJPanel());
  }

  protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs) throws NotConfigurableException {
    try {
      LRPredictorSettings statSettings = new LRPredictorSettings(settings) ;
      System.out.println("dialog load settings from " );
      System.out.println(statSettings);
    } catch (InvalidSettingsException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
    System.out.println("dialog saveSettingsTo..................");
    LRPredictorSettings statSettings = new LRPredictorSettings() ;
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