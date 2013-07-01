package org.saarus.knime.data.io.json;

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
import org.saarus.knime.ServiceContext;
import org.saarus.knime.data.io.json.JSONImportConfigs.JSONImportConfig;
import org.saarus.knime.uicomp.JInfoDialog;
import org.saarus.knime.uicomp.JTabbedPaneUI;
import org.saarus.service.util.JSONSerializer;

/** 
 * @author Tuan Nguyen
 */
public class JSONImportNodeDialog extends NodeDialogPane {
  final static public int WIDTH  = 750 ;
  final static public int HEIGHT = 500 ;
  
  private JTabbedPaneUI tabbedPane ;
  
  protected JSONImportNodeDialog() {
    getPanel().setPreferredSize(new Dimension(WIDTH, HEIGHT)) ;
    
    tabbedPane = new JTabbedPaneUI();
    tabbedPane.addAddButton(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JSONImportConfig config = new JSONImportConfig() ;
        config.setTable("Table"  + tabbedPane.getTabCount()) ;
        tabbedPane.addTabView(config.getTable(), new JSONImportFileJPanel(config));
      }
    }) ;
    JPanel panel = new JPanel(new BorderLayout()) ;
    panel.add(tabbedPane, BorderLayout.CENTER) ;
    panel.add(createToolBox(), BorderLayout.SOUTH) ;
    addTab("Import Files", panel);
  }
  
  private JPanel createToolBox() {
    JPanel panel = new JPanel(new FlowLayout()) ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Tools"));
    JButton viewScript = new JButton("View Script");
    viewScript.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        JInfoDialog dialog = new JInfoDialog() ;
        try {
          String json = JSONSerializer.JSON_SERIALIZER.toString(getJSONImportConfigs().getGeneratedTask()) ;
          dialog.setInfo(json) ;
        } catch(Exception ex) {
          ex.printStackTrace() ;
        }
        dialog.setVisible(true) ;
      }
    });
    
    JButton demoYelp = new JButton("Saarus Work Flow");
    demoYelp.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        int tabCount = tabbedPane.getTabCount() ;
        for(int i = tabCount - 1; i > 0; i--) {
          tabbedPane.removeTabAt(i) ;
        }
        JSONImportConfig[] config ={
          new JSONImportConfig("user_json", "create table user_json....", "/user/hive/yelpdb/json/training/user"),
          new JSONImportConfig("business_json", "create table business_json....", "/user/hive/yelpdb/json/training/business"),
          new JSONImportConfig("checkin_json", "create table checkin_json....", "/user/hive/yelpdb/json/training/checkin"),
          new JSONImportConfig("review_json", "create table review_json....", "/user/hive/yelpdb/json/training/review"),
        };
        for(JSONImportConfig sel : config) {
          tabbedPane.addTabView(sel.getTable(), new JSONImportFileJPanel(sel));
        }
        tabbedPane.setSelectedTab(1) ;
      }
    });
    panel.add(demoYelp) ;
    panel.add(viewScript) ;
    return panel ;
  }
  
  protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs) throws NotConfigurableException {
    System.out.println("dialog load settings from " );
    try {
      JSONImportConfigs configs = new JSONImportConfigs(settings) ;
      Iterator<JSONImportConfig> i = configs.getFileImportConfig().iterator() ;
      while(i.hasNext()) {
        JSONImportConfig config = i.next() ;
        tabbedPane.addTabView(config.getTable(), new JSONImportFileJPanel(config));
      }
      if(tabbedPane.getTabCount() == 1) {
        JSONImportConfig config = new JSONImportConfig() ;
        tabbedPane.addTabView(config.getTable(), new JSONImportFileJPanel(config));
      }
      tabbedPane.setSelectedTab(1) ;
      System.out.println(configs);
    } catch (InvalidSettingsException e) {
      e.printStackTrace();
    }
  }
  
  public JSONImportConfigs getJSONImportConfigs() {
    JSONImportConfigs configs = new JSONImportConfigs() ;
    int tabCount = tabbedPane.getTabCount() ;
    for(int i = 1; i < tabCount; i++) {
      JSONImportFileJPanel panel = (JSONImportFileJPanel) tabbedPane.getTabAt(i) ;
      configs.add(panel.getJSONImportConfig()) ;
    }
    return configs ;
  }

  @Override
  protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
    System.out.println("dialog saveSettingsTo..................");
    JSONImportConfigs configs = getJSONImportConfigs() ;
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