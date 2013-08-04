package org.saarus.knime.nlp.text.learner;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.saarus.knime.uicomp.SpringUtilities;
import org.saarus.service.nlp.NLPLiblinearTrainTextConfig;

public class TextLearnerPanel extends JPanel {
  final static int MAX_WIDTH = TextLearnerNodeDialog.WIDTH ;

  private JTextField descInput = new JTextField();
  private JTextField tableInput = new JTextField();
  private JTextField textFieldInput = new JTextField();
  private JTextField labelFieldInput = new JTextField();
  private JTextField modelLocInput = new JTextField();
  private JTextField tmpDirInput = new JTextField();
  
  public TextLearnerPanel() {
    setLayout(new BorderLayout()) ;
    add(createInputBox(), BorderLayout.CENTER);
  }

  public void init(TextLearnerConfigs configs) {
    NLPLiblinearTrainTextConfig config = configs.config ;
    descInput.setText(configs.description) ;
    tableInput.setText(config.getTable()) ;
    textFieldInput.setText(config.getTextField()) ;
    labelFieldInput.setText(config.getLabelField()) ;
    modelLocInput.setText(config.getModelOutputLoc()) ;
    tmpDirInput.setText(config.getTmpDir()) ;
  }
  
  private JPanel createInputBox() {
    JPanel panel = new JPanel(new SpringLayout()) ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Parameters"));
    addInput(panel, "Description", descInput) ;
    addInput(panel, "Table", tableInput) ;
    addInput(panel, "Text Fiel", textFieldInput) ;
    addInput(panel, "Label Fiel", labelFieldInput) ;
    addInput(panel, "Model Output Location", modelLocInput) ;
    addInput(panel, "Tmp Dir", tmpDirInput) ;
    SpringUtilities.makeCompactGrid(panel, /*rows, cols*/6, 2,/*initX, initY*/ 6, 6, /*xPad, yPad*/ 6, 6);       
    return panel ;
  }

  private void addInput(JPanel panel, String label, JComponent comp) {
    panel.add(new JLabel(label)) ;
    panel.add(comp) ;
  }
  
  public TextLearnerConfigs getTextLearnerConfigs() {
    TextLearnerConfigs configs = new TextLearnerConfigs() ;
    configs.description = descInput.getText() ;
    configs.config.setTable(tableInput.getText()) ;
    configs.config.setTextField(textFieldInput.getText()) ;
    configs.config.setLabelField(labelFieldInput.getText()) ;
    configs.config.setModelOutputLoc(modelLocInput.getText()) ;
    configs.config.setTmpDir(tmpDirInput.getText()) ;
    return configs ;
  }
}