package org.saarus.knime.mahout.lr.predictor;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.saarus.knime.mahout.lr.predictor.LRPredictorConfigs.MahoutConfig;
import org.saarus.knime.uicomp.SpringUtilities;

public class LRPredictorPanel extends JPanel {
  private JTextField nameInput = new JTextField();
  private JTextField descInput = new JTextField();
  private JTextField input = new JTextField("/jsonFile/to/donut.csv");  
  private JTextField output = new JTextField() ;
  private JTextField model = new JTextField("/jsonFile/to/file.model");
  private JTextArea  colHeaders = new JTextArea("colum1,column2,column3...");
  private JCheckBox  auc = new JCheckBox() ;
  private JCheckBox  confusion = new JCheckBox() ;
  private JCheckBox  clusterMode = new JCheckBox() ;

  public LRPredictorPanel() {
    setLayout(new BorderLayout()) ;
    add(createInputBox(),       BorderLayout.NORTH);
  }
  
  public void init(LRPredictorConfigs configs) {
    MahoutConfig config = configs.mahoutConfig ;
    nameInput.setText(config.name);
    descInput.setText(config.description);
    
    input.setText(config.input) ; 
    output.setText(config.output)  ;
    model.setText(config.model)  ;
    colHeaders.setText(config.colHeaders)  ;
    auc.setSelected(config.auc) ;
    confusion.setSelected(config.confusion) ;
    clusterMode.setSelected(config.clusterMode) ;
  }

  private JPanel createInputBox() {
    JPanel panel = new JPanel(new SpringLayout()) ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Parameters"));
    colHeaders.setRows(3) ;
    colHeaders.setLineWrap(true);
    colHeaders.setWrapStyleWord(true);
    JScrollPane scrollText = new JScrollPane(colHeaders);
    addInput(panel, "Name", nameInput) ;
    addInput(panel, "Description", descInput) ;
    addInput(panel, "Input", input) ;
    addInput(panel, "Output", output) ;
    addInput(panel, "Model", model) ;
    addInput(panel, "Column Headers", scrollText) ;
    addInput(panel, "Auc", auc) ;
    addInput(panel, "Confusion", confusion) ;
    addInput(panel, "Cluster Mode", clusterMode) ;
    
    SpringUtilities.makeCompactGrid(panel, /*rows, cols*/9, 2,/*initX, initY*/ 6, 6, 
                                    /*xPad, yPad*/6, 6);       
    return panel ;
  }
  
  public MahoutConfig getMahoutConfig() {
    MahoutConfig config = new MahoutConfig() ;
    config.name = nameInput.getText() ;
    config.description = descInput.getText() ;

    config.input = input.getText() ; 
    config.output = output.getText()  ;// 
    config.model = model.getText() ;
    config.colHeaders = colHeaders.getText() ;
    config.auc = auc.isSelected() ;
    config.confusion = confusion.isSelected() ;
    config.clusterMode = clusterMode.isSelected() ;
    
    return config ;
  }

  private void addInput(JPanel panel, String label, JComponent comp) {
    panel.add(new JLabel(label)) ;
    panel.add(comp) ;
  }
}