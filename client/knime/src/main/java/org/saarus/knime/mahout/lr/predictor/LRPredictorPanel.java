package org.saarus.knime.mahout.lr.predictor;

import java.awt.BorderLayout;
import javax.swing.JPanel;

import org.saarus.mahout.classifier.sgd.LogisticRegressionPredictorConfig;
import org.saarus.swing.BeanBindingJCheckBox;
import org.saarus.swing.BeanBindingJTextField;
import org.saarus.swing.SpringLayoutGridJPanel;

public class LRPredictorPanel extends JPanel {
  private BeanBindingJTextField<LRPredictorConfig> name , description ;
  
  private BeanBindingJTextField<LogisticRegressionPredictorConfig> input, output, modelLoc ;
  
  private BeanBindingJCheckBox<LogisticRegressionPredictorConfig>  auc, confusion, clusterMode ;

  private LRPredictorConfig config = new LRPredictorConfig() ;
  
  public LRPredictorPanel() {
    setLayout(new BorderLayout()) ;
    SpringLayoutGridJPanel paramPanel = new SpringLayoutGridJPanel() ;
    paramPanel.createBorder("Parameters");
    
    name = new BeanBindingJTextField<LRPredictorConfig>(config, "name") ;
    paramPanel.addRow("Name", name) ;
    
    description = new BeanBindingJTextField<LRPredictorConfig>(config, "description") ;
    paramPanel.addRow("Description", description) ;
    
    input = new BeanBindingJTextField<LogisticRegressionPredictorConfig>(config.predictConfig, "input") ;
    paramPanel.addRow("Input", input) ;
    
    output = new BeanBindingJTextField<LogisticRegressionPredictorConfig>(config.predictConfig, "output") ;
    paramPanel.addRow("Output", output) ;
    
    modelLoc = new BeanBindingJTextField<LogisticRegressionPredictorConfig>(config.predictConfig, "modelLocation") ;
    paramPanel.addRow("Model Location", modelLoc) ;
    
    auc = new BeanBindingJCheckBox<LogisticRegressionPredictorConfig>(config.predictConfig, "auc") ;
    paramPanel.addRow("Auc", auc) ;
    
    confusion = new BeanBindingJCheckBox<LogisticRegressionPredictorConfig>(config.predictConfig, "confusion") ;
    paramPanel.addRow("Confusion", confusion) ;
    
    clusterMode = new BeanBindingJCheckBox<LogisticRegressionPredictorConfig>(config.predictConfig, "clusterMode") ;
    paramPanel.addRow("Cluster Mode", clusterMode) ;
    
    paramPanel.makeGrid() ;
    add(paramPanel,       BorderLayout.NORTH);
  }
  
  public void reset(LRPredictorConfig config) {
    this.config = config ;
    LogisticRegressionPredictorConfig pconfig = config.predictConfig ;
    name.setBean(config);
    description.setBean(config);
    
    input.setBean(pconfig) ; 
    output.setBean(pconfig)  ;
    modelLoc.setBean(pconfig)  ;
    auc.setBean(pconfig) ;
    confusion.setBean(pconfig) ;
    clusterMode.setBean(pconfig) ;
  }

  public LRPredictorConfig getLRPredictorConfig() { return this.config ; }
}