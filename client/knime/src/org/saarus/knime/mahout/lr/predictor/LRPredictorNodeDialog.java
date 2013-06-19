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
import org.saarus.knime.mahout.lr.predictor.LRPredictorConfigs.MahoutConfig;
import org.saarus.knime.uicomp.JInfoDialog;
import org.saarus.service.util.JSONSerializer;

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
        MahoutConfig config = new MahoutConfig() ;
        config.name = "Yelp Features Predict";
        config.description = "Yelp Features Predict";
        config.input = "/user/hive/yelpdb/test" ; 
        config.output = "/tmp/yelp/working/output" ;
        config.model = "dfs:/tmp/yelp-features.model" ;
        config.colHeaders =  "review_id, stars, text,vote_funny,vote_useful,vote_cool,business_id,business_city,business_state,business_open,business_review_count,business_stars,user_id,user_review_count,user_average_stars,user_vote_funny,user_vote_useful,user_vote_cool" ;
        config.auc = true ;
        config.confusion = true ;
        config.clusterMode =  true ;
        LRPredictorConfigs configs = new LRPredictorConfigs() ;
        configs.mahoutConfig = config ;
        predictorPanel.init(configs) ;
      }
    });
    panel.add(saarusWorkFlowCluster) ;
    
    
    JButton saarusWorkFlowLocal = new JButton("Saarus Work Flow Local");
    saarusWorkFlowLocal.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        MahoutConfig config = new MahoutConfig() ;
        config.name = "Yelp Features Predict";
        config.description = "Yelp Features Predict";
        config.input = "data/yelptest" ; 
        config.output = "working/yelp/output" ;
        config.model = "dfs:/tmp/yelp-features.model" ;
        config.colHeaders =  "stars, text, vote_funny, vote_useful, vote_cool, "+
                             "business_id, business_city, business_state, business_open, business_review_count, " +
                             "business_stars, user_review_count, user_average_stars" ;
        config.auc = true ;
        config.confusion = true ;
        config.clusterMode = false ;
        LRPredictorConfigs configs = new LRPredictorConfigs() ;
        configs.mahoutConfig = config ;
        predictorPanel.init(configs) ;
      }
    });
    panel.add(saarusWorkFlowLocal) ;
    
    JButton donut = new JButton("Donut");
    donut.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        MahoutConfig config = new MahoutConfig() ;
        config.name = "Donut Predict";
        config.description = "Donut Test Data";
        config.input = "data/donutmr" ; 
        config.output = "working/donut/output" ;
        config.model = "dfs:/tmp/donut.model" ;
        config.colHeaders = "x,y,shape,color,xx,xy,yy,c,a,b" ;
        config.auc = true ;
        config.confusion = true ;
        LRPredictorConfigs configs = new LRPredictorConfigs() ;
        configs.mahoutConfig = config ;
        predictorPanel.init(configs) ;
      }
    });
    panel.add(donut) ;
    
    JButton viewScript = new JButton("View Script");
    viewScript.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        JInfoDialog dialog = new JInfoDialog() ;
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

  
  public LRPredictorConfigs getLRPredictorConfigs() {
    LRPredictorConfigs configs = new LRPredictorConfigs() ;
    configs.mahoutConfig = predictorPanel.getMahoutConfig() ;
    return configs ;
  }
  
  protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs) throws NotConfigurableException {
    System.out.println("dialog load settings from " );
    try {
      LRPredictorConfigs configs = new LRPredictorConfigs(settings) ;
      predictorPanel.init(configs) ;
    } catch (InvalidSettingsException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
    System.out.println("dialog saveSettingsTo..................");
    LRPredictorConfigs configs = getLRPredictorConfigs() ;
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