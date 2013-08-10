package org.saarus.knime.mahout.lr.learner;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.saarus.mahout.classifier.sgd.LogisticRegressionTrainerConfig;
import org.saarus.swing.JInfoDialog;
import org.saarus.util.json.JSONSerializer;

/**
 * @author Tuan Nguyen
 */
public class LRLearnerNodeDialog extends NodeDialogPane {
  final static public int WIDTH  = 750 ;
  final static public int HEIGHT = 500 ;
  
  private LRLearnerPanel learnerPanel ;
  
  protected LRLearnerNodeDialog() {
    getPanel().setPreferredSize(new Dimension(WIDTH, HEIGHT)) ;
    JPanel panel = new JPanel(new BorderLayout()) ;
    learnerPanel = new LRLearnerPanel() ;
    panel.add(learnerPanel, BorderLayout.CENTER) ;
    panel.add(createToolBox(), BorderLayout.SOUTH) ;
    addTab("Logistic Regression Learner", panel);
  }

  private JPanel createToolBox() {
    JPanel panel = new JPanel(new FlowLayout()) ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Tools"));
    
    JButton saarusWorkFlow = new JButton("Saarus Work Flow");
    saarusWorkFlow.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        LRLearnerConfig configs = new LRLearnerConfig() ;
        configs.name = "Yelp Features";
        configs.description = "Yelp Features Data";
        LogisticRegressionTrainerConfig config = new LogisticRegressionTrainerConfig() ;
        config.setInput("hive://features") ;
        config.setModelOutputLocation("dfs:/tmp/yelp-lr-model") ;
        config.setTarget("cat_useful") ;
        config.setCategories("2") ;
        config.addPredictor("user_review_count", "n").
               addPredictor("user_average_stars", "n").
               addPredictor("user_vote_useful", "n").
               addPredictor("stars", "n").
               addPredictor("business_stars", "n").
               addPredictor("business_review_count", "n").
               addPredictor("vote_useful", "n").
               addPredictor("vote_funny", "n").
               addPredictor("vote_cool", "n").
               addPredictor("percentage_useful", "n");
        config.setFeatures("1000");
        config.setPasses("100") ;
        config.setRate("50") ;
        config.setLambda("0.001") ;
        configs.trainConfig = config ;
        learnerPanel.reset(configs) ;
      }
    });
    panel.add(saarusWorkFlow) ;
    
    JButton viewScript = new JButton("View Script");
    viewScript.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        JInfoDialog dialog = JInfoDialog.getInstance() ;
        try {
          String json = JSONSerializer.JSON_SERIALIZER.toString(getLRLearnerConfigs().getGeneratedTask()) ;
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

  public LRLearnerConfig getLRLearnerConfigs() {
    return learnerPanel.getLRLearnerConfig() ;
  }
  
  protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs) throws NotConfigurableException {
    System.out.println("dialog load settings from " );
    try {
      LRLearnerConfig configs = new LRLearnerConfig(settings) ;
      learnerPanel.reset(configs) ;
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
    System.out.println("dialog saveSettingsTo..................");
    LRLearnerConfig configs = getLRLearnerConfigs() ;
    configs.saveSettings(settings) ;
    System.out.println("dialog saveSettingsTo.................. done");
  }

  
  public void onClose() {
    System.out.println("-----------------------close diaglog-------------------------") ;
  }

  public void onOpen() {
    System.out.println("-----------------------open dialog--------------------------") ;
  }
}