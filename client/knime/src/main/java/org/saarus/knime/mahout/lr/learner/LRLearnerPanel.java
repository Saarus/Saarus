package org.saarus.knime.mahout.lr.learner;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.saarus.knime.mahout.lr.learner.LRLearnerConfigs.MahoutConfig;
import org.saarus.knime.uicomp.SpringUtilities;

public class LRLearnerPanel extends JPanel {
  final static int MAX_WIDTH = LRLearnerNodeDialog.WIDTH ;

  private JTextField nameInput = new JTextField();
  private JTextField descInput = new JTextField();
  
  private JTextField passes = new JTextField("100"); //  --passes 100,
  private JTextField rate   = new JTextField("50");  // --rate 50
  private JTextField lambda = new JTextField() ;  //--lambda 0.001
  private JTextField features = new JTextField("21"); // --features 21
  private JTextField categories = new JTextField("2"); //  --categories 2
  //--predictors  x, y, xx, xy, yy, a, b, c
  private JTextField predictors = new JTextField("x, y, xx, xy, yy, a, b, c") ; 
  private JTextField input = new JTextField("/jsonFile/to/donut.csv"); //--input donut.csv 
  private JComboBox<String> target = new JComboBox<String>() ; //--target color
  private JTextField output = new JTextField() ;// --output donut.model
 
  public LRLearnerPanel() {
    setLayout(new BorderLayout()) ;
    target.setEditable(true) ;
    add(createInputBox(), BorderLayout.CENTER);
  }

  public void init(LRLearnerConfigs configs) {
    MahoutConfig config = configs.mahoutConfig ;
    nameInput.setText(config.name);
    descInput.setText(config.description);
    
    passes.setText(config.passes) ; 
    rate.setText(config.rate) ;  
    lambda.setText(config.lambda) ; 
    features.setText(config.features) ;
    categories.setText(config.categories); 
    predictors.setText(config.predictors); 
    input.setText(config.input) ; 
    target.setModel(new DefaultComboBoxModel<String>(new String[] {config.target})) ;
    target.setSelectedItem(config.target) ; 
    output.setText(config.output)  ;// --output donut.model
  }
  
  private JPanel createInputBox() {
    predictors.addMouseListener(new MouseAdapter() {
      public void mousePressed(MouseEvent e) {
        if(e.getClickCount() > 1) {
          FieldSelector selector = new FieldSelector() ;
          selector.setLocationRelativeTo(predictors) ;
          selector.setVisible(true) ;
        }
      }
    });
    
    JPanel panel = new JPanel(new SpringLayout()) ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Parameters"));
    addInput(panel, "Name", nameInput) ;
    addInput(panel, "Description", descInput) ;
    addInput(panel, "Passes", passes) ;
    addInput(panel, "Rate", rate) ;
    addInput(panel, "Lambda", lambda) ;
    addInput(panel, "Features", features) ;
    addInput(panel, "Categories", categories) ;
    addInput(panel, "Predictors", predictors) ;
    addInput(panel, "Input Location", input) ;
    addInput(panel, "Target", target) ;
    addInput(panel, "Output", output) ;
    SpringUtilities.makeCompactGrid(panel, /*rows, cols*/11, 2,/*initX, initY*/ 6, 6, 
                                    /*xPad, yPad*/ 6, 6);       
    return panel ;
  }

  private void addInput(JPanel panel, String label, JComponent comp) {
    panel.add(new JLabel(label)) ;
    panel.add(comp) ;
  }
  
  public MahoutConfig getMahoutConfig() {
    MahoutConfig config = new MahoutConfig() ;
    config.name = nameInput.getText() ;
    config.description = descInput.getText() ;

    config.passes = passes.getText() ; 
    config.rate = rate.getText() ;  
    config.lambda = lambda.getText() ; 
    config.features = features.getText() ;
    config.categories = categories.getText(); 
    config.predictors = predictors.getText(); 
    config.input = input.getText() ; 
    config.target = (String) target.getSelectedItem() ; 
    config.output = output.getText()  ;// 
    return config ;
  }
  
  static public class SelectTableListener implements ItemListener {
    public void itemStateChanged(ItemEvent event) {
    }
  }
  
  static public class FieldSelector extends JDialog {
    public FieldSelector() {
      setTitle("Select The Fields") ;
      setMinimumSize(new Dimension(150, 350)) ;
      setLayout(new BorderLayout()) ;
      setAlwaysOnTop(true) ;
      JButton close = new JButton("OK") ;
      close.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
          onClose() ;
        }
      }) ;
      JPanel algPanel = new JPanel() ;
      algPanel.setLayout(new SpringLayout()) ;
      String[] fields = {
        "review_id", "user_id", "user_name", "business_id", "business_name",
        "vote_funny", "vote_useful", "vote_cool"
      } ;
      for(int i = 0; i < fields.length; i++) {
        JCheckBox checkBox = new JCheckBox() ;
        checkBox.setName(fields[i]) ;
        algPanel.add(checkBox) ;
        algPanel.add(new JLabel(fields[i])) ;
      }
      SpringUtilities.makeCompactGrid(algPanel, /*rows, cols*/fields.length, 2,  
                                     /*initX, initY*/ 6, 6, /*xPad, yPad*/6, 6);
      add(algPanel, BorderLayout.CENTER) ;
      add(close, BorderLayout.SOUTH) ;
    }
    
    public void onClose() {
      dispose() ;
    }
  }
}