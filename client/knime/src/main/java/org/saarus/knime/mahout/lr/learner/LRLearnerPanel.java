package org.saarus.knime.mahout.lr.learner;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.saarus.mahout.classifier.sgd.LogisticRegressionTrainerConfig;
import org.saarus.mahout.classifier.sgd.LogisticRegressionTrainerConfig.Predictor;
import org.saarus.swing.BeanBindingJTable;
import org.saarus.swing.BeanBindingJTextField;
import org.saarus.swing.SpringLayoutGridJPanel;
import org.saarus.swing.sql.model.SQLTable.Field;

public class LRLearnerPanel extends JPanel {
  final static int MAX_WIDTH = LRLearnerNodeDialog.WIDTH ;

  private BeanBindingJTextField<LRLearnerConfig> nameInput ;
  private BeanBindingJTextField<LRLearnerConfig> descInput ;
  
  private BeanBindingJTextField<LogisticRegressionTrainerConfig> passes ; 
  private BeanBindingJTextField<LogisticRegressionTrainerConfig> rate   ;
  private BeanBindingJTextField<LogisticRegressionTrainerConfig> lambda ; 
  private BeanBindingJTextField<LogisticRegressionTrainerConfig> features ; // --features 21
  private BeanBindingJTextField<LogisticRegressionTrainerConfig> categories ;
  private PredictorPanel predictorPanel ;
  private BeanBindingJTextField<LogisticRegressionTrainerConfig> input ;
  private BeanBindingJTextField<LogisticRegressionTrainerConfig> target ;
  private BeanBindingJTextField<LogisticRegressionTrainerConfig> output  ;// --output donut.model
  LRLearnerConfig config ;
  
  public LRLearnerPanel() {
    setLayout(new BorderLayout())  ;
    config = new LRLearnerConfig();
    
    SpringLayoutGridJPanel panel = new SpringLayoutGridJPanel() ;
    panel.createBorder("Parameters") ;
    
    nameInput = new BeanBindingJTextField<LRLearnerConfig>(config, "name") ;
    panel.addRow("Name", nameInput) ;
    
    descInput = new BeanBindingJTextField<LRLearnerConfig>(config, "description") ;
    panel.addRow("Description", descInput) ;
    
    passes = new BeanBindingJTextField<LogisticRegressionTrainerConfig>(config.trainConfig, "passes") ;
    panel.addRow("Passes", passes) ;
    
    rate = new BeanBindingJTextField<LogisticRegressionTrainerConfig>(config.trainConfig, "rate") ;
    panel.addRow("Rate", rate) ;
    
    lambda = new BeanBindingJTextField<LogisticRegressionTrainerConfig>(config.trainConfig, "lambda") ;
    panel.addRow("Lambda", lambda) ;
    
    features = new BeanBindingJTextField<LogisticRegressionTrainerConfig>(config.trainConfig, "features") ;
    panel.addRow("Features", features) ;
    
    categories = new BeanBindingJTextField<LogisticRegressionTrainerConfig>(config.trainConfig, "categories") ;
    panel.addRow("Categories", categories) ;
    
    input = new BeanBindingJTextField<LogisticRegressionTrainerConfig>(config.trainConfig, "input") ;
    panel.addRow("Input ", input) ;
    
    predictorPanel = new PredictorPanel(config.trainConfig) ;
    panel.addRow("Predictors", predictorPanel) ;
    
    target = new BeanBindingJTextField<LogisticRegressionTrainerConfig>(config.trainConfig, "target") ;
    panel.addRow("Target", target) ;
    
    output = new BeanBindingJTextField<LogisticRegressionTrainerConfig>(config.trainConfig, "output") ;
    panel.addRow("Output", output) ;
    panel.makeGrid() ;
    add(panel, BorderLayout.CENTER);
  }

  public void reset(LRLearnerConfig config) {
    this.config =  config ;
    LogisticRegressionTrainerConfig trainerConfig = config.trainConfig ;
    nameInput.setBean(config);
    descInput.setBean(config);
    
    passes.setBean(trainerConfig) ; 
    rate.setBean(trainerConfig) ;  
    lambda.setBean(trainerConfig) ; 
    features.setBean(trainerConfig) ;
    categories.setBean(trainerConfig); 
    predictorPanel.update(trainerConfig) ;
    input.setBean(trainerConfig) ; 
    target.setBean(trainerConfig) ; 
    output.setBean(trainerConfig)  ;// --output donut.model
  }
  

  public LRLearnerConfig getLRLearnerConfig() { return this.config ; }
  
  static public class PredictorPanel extends JPanel {
    private PredictorPanelTable table ;
    
    public PredictorPanel(LogisticRegressionTrainerConfig trainerConfig) {
      setLayout(new BorderLayout()) ;
      setPreferredSize(new Dimension(500, 300)); 
      JToolBar toolbar = new JToolBar() ;
      Action detectAction = new AbstractAction("Detect") {
        public void actionPerformed(ActionEvent arg0) {
          
        }
      };
      toolbar.add(detectAction) ;
      add(toolbar, BorderLayout.NORTH) ;
      
      table = new PredictorPanelTable(trainerConfig) ;
      add(new JScrollPane(table), BorderLayout.CENTER) ;
    }
    
    void update(LogisticRegressionTrainerConfig trainerConfig) {
      table.update(trainerConfig) ;
    }
  }
  
  static public class PredictorPanelTable extends BeanBindingJTable<PredictorMapping> {
    static String[] COLUMN_NAMES  = {"Select", "name", "type"} ;
    static String[] BEAN_PROPERTY = {"select", "name", "type"} ;
    
    private LogisticRegressionTrainerConfig trainerConfig ;
    
    public PredictorPanelTable(LogisticRegressionTrainerConfig trainerConfig) {
      this.trainerConfig = trainerConfig ;
      init(COLUMN_NAMES, BEAN_PROPERTY, new ArrayList<PredictorMapping>()) ;
      getColumnModel().getColumn(1).setWidth(25) ;
      update(trainerConfig) ;
      createRowPopupMenu() ;
    }
    
    protected boolean isBeanEditableAt(int row, int col) {
      return true ;
    }

    protected PredictorMapping newBean() { return new PredictorMapping(false, null, null) ; }
  
    public boolean onAddRow() { 
      beans.add(new PredictorMapping(false, null, null));
      updatePredictor() ;
      return true;  
    }
    
    public boolean onRemoveRow(Field bean, int row) { 
      beans.remove(row) ;
      updatePredictor() ;
      return true ; 
    }
    
    void updatePredictor() {
      List<Predictor> predictors = trainerConfig.getPredictors() ;
      predictors.clear() ;
      for(int i = 0 ; i < beans.size(); i++) {
        PredictorMapping pm = beans.get(i) ;
        if(pm.isSelect())  predictors.add(new Predictor(pm.getName(), pm.getType())) ;
      }
    }
    
    void update(LogisticRegressionTrainerConfig trainerConfig) {
      beans.clear() ;
      List<Predictor> predictors = trainerConfig.getPredictors() ;
      for(int i = 0; i < predictors.size(); i++) {
        Predictor predictor = predictors.get(i) ;
        beans.add(new PredictorMapping(true, predictor.getName(), predictor.getType())) ;
      }
      if(beans.size() == 0) {
        beans.add(new PredictorMapping(false, null, null)) ;
      }
      fireTableDataChanged() ;
    }
  }
  
  static public class PredictorMapping {
    private boolean select ;
    private String  name   ;
    private String  type   ;
    
    public PredictorMapping(boolean select, String name, String type) {
      this.select = select ;
      this.name = name ;
      this.type = type ;
    }
    
    public boolean isSelect() { return select; }
    public void setSelect(boolean select) { this.select = select; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
  }
}