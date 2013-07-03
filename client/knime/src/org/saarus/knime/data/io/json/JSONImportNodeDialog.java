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
        JInfoDialog dialog = JInfoDialog.getInstance() ;
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
        
        JSONImportConfig userTableConfig =
          new JSONImportConfig("user", "create table user....", "dfs:/user/hive/yelpdb/json/training/user/data.json") ;
        userTableConfig.addFieldConfig("user_id", "STRING", "user_id") ;
        userTableConfig.addFieldConfig("name", "STRING", "name") ;
        userTableConfig.addFieldConfig("review_count", "INT", "review_count") ;
        userTableConfig.addFieldConfig("average_stars", "FLOAT", "average_stars") ;
        userTableConfig.addFieldConfig("vote_funny",  "INT", "votes.funny") ;
        userTableConfig.addFieldConfig("vote_useful", "INT", "votes.useful") ;
        userTableConfig.addFieldConfig("vote_cool",   "INT", "votes.cool") ;
        
        JSONImportConfig businessTableConfig =
            new JSONImportConfig("business", "create table business", "dfs:/user/hive/yelpdb/json/training/business/data.json") ;
        businessTableConfig.addFieldConfig("business_id", "STRING", "business_id") ;
        businessTableConfig.addFieldConfig("name", "STRING", "name") ;
        businessTableConfig.addFieldConfig("full_address", "STRING", "full_address") ;
        businessTableConfig.addFieldConfig("city", "STRING", "city") ;
        businessTableConfig.addFieldConfig("state", "STRING", "state") ;
        businessTableConfig.addFieldConfig("open", "INT", "open") ;
        businessTableConfig.addFieldConfig("longitude", "DOUBLE", "longitude") ;
        businessTableConfig.addFieldConfig("latitude", "DOUBLE", "latitude") ;
        businessTableConfig.addFieldConfig("categories", "STRING", "categories") ;
        businessTableConfig.addFieldConfig("review_count", "INT", "review_count") ;
        businessTableConfig.addFieldConfig("stars", "FLOAT", "stars") ;
        businessTableConfig.addFieldConfig("neighborhoods", "STRING", "neighborhoods") ;
        
        JSONImportConfig checkinTableConfig =
            new JSONImportConfig("checkin", "create table checkin", "dfs:/user/hive/yelpdb/json/training/checkin/data.json") ;
        checkinTableConfig.addFieldConfig("business_id", "STRING", "business_id") ;
        checkinTableConfig.addFieldConfig("checkin_info", "STRING", "checkin_info") ;
        
        JSONImportConfig reviewTableConfig =
            new JSONImportConfig("review", "create table review", "dfs:/user/hive/yelpdb/json/training/review/data.json") ;
        reviewTableConfig.addFieldConfig("review_id", "STRING", "review_id") ;
        reviewTableConfig.addFieldConfig("business_id", "STRING", "business_id") ;
        reviewTableConfig.addFieldConfig("user_id", "STRING", "user_id") ;
        reviewTableConfig.addFieldConfig("stars", "FLOAT", "stars") ;
        reviewTableConfig.addFieldConfig("text", "STRING", "text") ;
        reviewTableConfig.addFieldConfig("`date`", "STRING", "date") ;
        reviewTableConfig.addFieldConfig("vote_funny", "INT", "votes.funny") ;
        reviewTableConfig.addFieldConfig("vote_useful", "INT", "votes.useful") ;
        reviewTableConfig.addFieldConfig("vote_cool", "INT", "votes.cool") ;
        
        JSONImportConfig[] config ={
          userTableConfig, businessTableConfig, checkinTableConfig, reviewTableConfig 
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
      configs.addConfig(panel.getJSONImportConfig()) ;
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