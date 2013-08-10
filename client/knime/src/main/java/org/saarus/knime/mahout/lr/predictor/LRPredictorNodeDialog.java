package org.saarus.knime.mahout.lr.predictor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.saarus.mahout.classifier.sgd.LogisticRegressionPredictorConfig;
import org.saarus.swing.JInfoDialog;
import org.saarus.util.json.JSONSerializer;

/**
 * @author Tuan Nguyen
 */
public class LRPredictorNodeDialog extends NodeDialogPane {
  final static public int WIDTH  = 750 ;
  final static public int HEIGHT = 500 ;
  
  private LRPredictorPanel predictorPanel ;
  
  protected LRPredictorNodeDialog() {
    getPanel().setPreferredSize(new Dimension(WIDTH, HEIGHT)) ;
    predictorPanel = new LRPredictorPanel() ;
    
    JPanel panel = new JPanel(new BorderLayout()) ;
    panel.add(predictorPanel, BorderLayout.CENTER) ;
    panel.add(createToolBox(), BorderLayout.SOUTH) ;
    
    addTab("Logistic Regression Predictor", panel);
  }

  private JPanel createToolBox() {
    JPanel panel = new JPanel(new FlowLayout()) ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Tools"));
    
    JButton saarusWorkFlowCluster = new JButton("Saarus Work Flow");
    saarusWorkFlowCluster.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        LRPredictorConfig config = getYelpPredictConfig() ;
        predictorPanel.reset(config) ;
        predictorPanel.reset(config) ;
      }
    });
    panel.add(saarusWorkFlowCluster) ;
    
    
    JButton saarusWorkFlowLocal = new JButton("Saarus Work Flow Local");
    saarusWorkFlowLocal.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        LRPredictorConfig config = getYelpPredictConfig() ;
        config.predictConfig.setClusterMode(false) ;
        predictorPanel.reset(config) ;
      }
    });
    panel.add(saarusWorkFlowLocal) ;
    
    JButton viewScript = new JButton("View Script");
    viewScript.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        JInfoDialog dialog = JInfoDialog.getInstance() ;
        try {
          String json = JSONSerializer.JSON_SERIALIZER.toString(getLRPredictorConfigs().getGeneratedTask()) ;
          dialog.setInfo(json) ;
        } catch(Exception ex) {
          ex.printStackTrace() ;
        }
        dialog.setVisible(true) ;
      }
    });
    panel.add(viewScript) ;
    
    return panel ;
  }

  
  public LRPredictorConfig getLRPredictorConfigs() {
    return predictorPanel.getLRPredictorConfig() ;
  }
  
  protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs) throws NotConfigurableException {
    System.out.println("dialog load settings from " );
    try {
      LRPredictorConfig configs = new LRPredictorConfig(settings) ;
      predictorPanel.reset(configs) ;
    } catch (InvalidSettingsException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
    System.out.println("dialog saveSettingsTo..................");
    LRPredictorConfig configs = getLRPredictorConfigs() ;
    configs.saveSettings(settings) ;
    System.out.println("dialog saveSettingsTo.................. done");
  }
  
  public void onClose() {
    System.out.println("-----------------------close diaglog-------------------------") ;
  }

  public void onOpen() {
    System.out.println("-----------------------open dialog--------------------------") ;
  }
  
  private LRPredictorConfig getYelpPredictConfig() {
    LRPredictorConfig config = new LRPredictorConfig() ;
    config.setName("Yelp Features Predict");
    config.setDescription("Yelp Features Predict");
    LogisticRegressionPredictorConfig predictConfig = new LogisticRegressionPredictorConfig() ;
    predictConfig.setInput("/user/hive/yelpdb/test") ; 
    predictConfig.setOutput("/tmp/yelp/working/output") ;
    predictConfig.setModelLocation("dfs:/tmp/yelp-lr-model") ;
    predictConfig.setAuc(true) ;
    predictConfig.setConfusion(true) ;
    predictConfig.setClusterMode(true) ;
    
    config.predictConfig = predictConfig ;
    return config ;
  }
}