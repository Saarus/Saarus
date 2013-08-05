package org.saarus.knime.data.stat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.saarus.knime.data.stat.StatisticConfigs.StatisticConfig;
import org.saarus.knime.uicomp.JInfoDialog;
import org.saarus.knime.uicomp.JTabbedPaneUI;
import org.saarus.util.json.JSONSerializer;
/**
 * @author Tuan Nguyen
 */
public class StatisticNodeDialog extends NodeDialogPane {
  final static public int WIDTH  = 750 ;
  final static public int HEIGHT = 500 ;
  private JTabbedPaneUI tabbedPane ;
  
  protected StatisticNodeDialog() {
    getPanel().setPreferredSize(new Dimension(WIDTH, HEIGHT)) ;

    tabbedPane = new JTabbedPaneUI();
    tabbedPane.addAddButton(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        StatisticConfig config = new StatisticConfig("Table " + tabbedPane.getTabCount(), "", "") ;
        tabbedPane.addTabView(config.table, new StatisticConfigPanel(config));
      }
    }) ;
    JPanel panel = new JPanel(new BorderLayout()) ;
    panel.add(tabbedPane, BorderLayout.CENTER) ;
    panel.add(createToolBox(), BorderLayout.SOUTH) ;
    addTab("Table Field Statistic", panel);
  }
  
  private JPanel createToolBox() {
    JPanel panel = new JPanel(new FlowLayout()) ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Tools"));
    JButton viewScript = new JButton("View Script");
    viewScript.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        JInfoDialog dialog = JInfoDialog.getInstance() ;
        try {
          String json = JSONSerializer.JSON_SERIALIZER.toString(getStatisticConfigs().getGeneratedTask()) ;
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

  public StatisticConfigs getStatisticConfigs() {
    StatisticConfigs configs = new StatisticConfigs() ;
    int tabCount = tabbedPane.getTabCount() ;
    for(int i = 1; i < tabCount; i++) {
      StatisticConfigPanel panel = (StatisticConfigPanel) tabbedPane.getTabAt(i) ;
      configs.add(panel.getStatisticConfig()) ;
    }
    return configs ;
  }
  
  protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs) throws NotConfigurableException {
    System.out.println("dialog load settings from " );
    try {
      StatisticConfigs configs = new StatisticConfigs(settings) ;
      Iterator<StatisticConfig> i = configs.getConfigs().iterator() ;
      while(i.hasNext()) {
        StatisticConfig config = i.next() ;
        tabbedPane.addTabView(config.table, new StatisticConfigPanel(config));
      }
      if(tabbedPane.getTabCount() == 1) {
        StatisticConfig config = new StatisticConfig("Table " + tabbedPane.getTabCount(), "", "") ;
        tabbedPane.addTabView(config.table, new StatisticConfigPanel(config));
      }
      tabbedPane.setSelectedTab(1) ;
      System.out.println(configs);
    } catch (InvalidSettingsException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
    System.out.println("dialog saveSettingsTo..................");
    StatisticConfigs configs = getStatisticConfigs() ;
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