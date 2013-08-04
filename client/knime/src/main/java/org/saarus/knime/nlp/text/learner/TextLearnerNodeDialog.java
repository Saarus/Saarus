package org.saarus.knime.nlp.text.learner;

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
import org.saarus.knime.uicomp.JInfoDialog;
import org.saarus.service.util.JSONSerializer;

/**
 * @author Tuan Nguyen
 */
public class TextLearnerNodeDialog extends NodeDialogPane {
  final static public int WIDTH  = 750 ;
  final static public int HEIGHT = 500 ;
  
  private TextLearnerPanel learnerPanel ;
  
  protected TextLearnerNodeDialog() {
    getPanel().setPreferredSize(new Dimension(WIDTH, HEIGHT)) ;
    JPanel panel = new JPanel(new BorderLayout()) ;
    learnerPanel = new TextLearnerPanel() ;
    panel.add(learnerPanel, BorderLayout.CENTER) ;
    panel.add(createToolBox(), BorderLayout.SOUTH) ;
    addTab("Text Learner", panel);
  }

  private JPanel createToolBox() {
    JPanel panel = new JPanel(new FlowLayout()) ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Tools"));
    
    JButton saarusWorkFlow = new JButton("Saarus Twitter");
    saarusWorkFlow.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        TextLearnerConfigs learnerConfig = new TextLearnerConfigs() ;
        learnerConfig.description = "Train Twitter positive/negative sentiment" ;
        learnerConfig.config.setTable("twitter") ;
        learnerConfig.config.setTextField("sentimenttext") ;
        learnerConfig.config.setLabelField("sentiment") ;
        learnerConfig.config.setModelOutputLoc("dfs:/tmp/twitter/model") ;
        learnerConfig.config.setTmpDir("target/nlpTEMP") ;
        learnerPanel.init(learnerConfig) ;
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

  public TextLearnerConfigs getLRLearnerConfigs() {
    return learnerPanel.getTextLearnerConfigs() ;
  }
  
  protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs) throws NotConfigurableException {
    System.out.println("dialog load settings from " );
    try {
      TextLearnerConfigs configs = new TextLearnerConfigs(settings) ;
      learnerPanel.init(configs) ;
    } catch (InvalidSettingsException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
    System.out.println("dialog saveSettingsTo..................");
    TextLearnerConfigs configs = getLRLearnerConfigs() ;
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