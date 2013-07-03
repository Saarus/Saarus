package org.saarus.knime.mahout.lr.learner;

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
import org.saarus.knime.mahout.lr.learner.LRLearnerConfigs.MahoutConfig;
import org.saarus.knime.uicomp.JInfoDialog;
import org.saarus.service.util.JSONSerializer;

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
        MahoutConfig config = new MahoutConfig() ;
        config.name = "Yelp Features";
        config.description = "Yelp Features Data";
        config.passes = "100" ; 
        config.rate = "50" ;  
        config.lambda = "0.001" ; 
        config.features = "1000" ;
        config.categories = "2"; 
        config.predictors = "n:user_review_count | n:user_average_stars | n:user_vote_useful | n:stars | n:business_stars | n:business_review_count | n:vote_useful | n:vote_funny | n:vote_cool | n:percentage_useful" ; 
        config.input = "hive://features" ; 
        config.target = "cat_useful"; 
        config.output = "dfs:/tmp/yelp-features.model" ;
        LRLearnerConfigs configs = new LRLearnerConfigs() ;
        configs.mahoutConfig = config ;
        learnerPanel.init(configs) ;
      }
    });
    panel.add(saarusWorkFlow) ;
    
    JButton donut = new JButton("Donut");
    donut.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        MahoutConfig config = new MahoutConfig() ;
        config.name = "Donut Data";
        config.description = "Donut Sample Data";
        config.passes = "100" ; 
        config.rate = "50" ;  
        config.lambda = "0.001" ; 
        config.features = "20" ;
        config.categories = "2"; 
        config.predictors = "n:x | n:y | n:xx | n:xy | n:yy | n:a | n:b | n:c" ; 
        config.input = "hive://donut_train" ; 
        config.target = "color"; 
        config.output = "dfs:/tmp/donut.model" ;
        LRLearnerConfigs configs = new LRLearnerConfigs() ;
        configs.mahoutConfig = config ;
        learnerPanel.init(configs) ;
      }
    });
    panel.add(donut) ;
    
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

  public LRLearnerConfigs getLRLearnerConfigs() {
    LRLearnerConfigs configs = new LRLearnerConfigs() ;
    configs.mahoutConfig = learnerPanel.getMahoutConfig() ;
    return configs ;
  }
  
  protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs) throws NotConfigurableException {
    System.out.println("dialog load settings from " );
    try {
      LRLearnerConfigs configs = new LRLearnerConfigs(settings) ;
      learnerPanel.init(configs) ;
    } catch (InvalidSettingsException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
    System.out.println("dialog saveSettingsTo..................");
    LRLearnerConfigs configs = getLRLearnerConfigs() ;
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