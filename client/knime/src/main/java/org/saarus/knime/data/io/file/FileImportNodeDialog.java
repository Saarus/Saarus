package org.saarus.knime.data.io.file;

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
import org.saarus.knime.data.io.file.FileImportConfigs.FileImportConfig;
import org.saarus.swing.JInfoDialog;
import org.saarus.swing.JTabbedPaneUI;
import org.saarus.util.json.JSONSerializer;

/** 
 * @author Tuan Nguyen
 */
public class FileImportNodeDialog extends NodeDialogPane {
  final static public int WIDTH  = 750 ;
  final static public int HEIGHT = 500 ;
  
  private JTabbedPaneUI tabbedPane ;
  
  protected FileImportNodeDialog() {
    getPanel().setPreferredSize(new Dimension(WIDTH, HEIGHT)) ;
    
    tabbedPane = new JTabbedPaneUI();
    tabbedPane.addAddButton(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        FileImportConfig config = new FileImportConfig() ;
        config.setTable("Table"  + tabbedPane.getTabCount()) ;
        tabbedPane.addTabView(config.getTable(), new FileImportPanel(config));
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
          String json = JSONSerializer.JSON_SERIALIZER.toString(getFileImportConfigs().generatedTask()) ;
          dialog.setInfo(json) ;
        } catch(Exception ex) {
          ex.printStackTrace() ;
        }
        dialog.setVisible(true) ;
      }
    });
    
    JButton demoYelp = new JButton("Saarus WorkFlow");
    demoYelp.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        int tabCount = tabbedPane.getTabCount() ;
        for(int i = tabCount - 1; i > 0; i--) {
          tabbedPane.removeTabAt(i) ;
        }
        
        FileImportConfig userTableConfig =
          new FileImportConfig("user", "create table user....", "dfs:/user/hive/yelpdb/json/training/user/data.json", "Json") ;
        userTableConfig.addFieldConfig("user_id", "STRING", "user_id") ;
        userTableConfig.addFieldConfig("name", "STRING", "name") ;
        userTableConfig.addFieldConfig("review_count", "INT", "review_count") ;
        userTableConfig.addFieldConfig("average_stars", "FLOAT", "average_stars") ;
        userTableConfig.addFieldConfig("vote_funny",  "INT", "votes.funny") ;
        userTableConfig.addFieldConfig("vote_useful", "INT", "votes.useful") ;
        userTableConfig.addFieldConfig("vote_cool",   "INT", "votes.cool") ;
        
        FileImportConfig businessTableConfig =
            new FileImportConfig("business", "create table business", "dfs:/user/hive/yelpdb/json/training/business/data.json", "Json") ;
        businessTableConfig.addFieldConfig("business_id", "STRING", "business_id") ;
        businessTableConfig.addFieldConfig("name", "STRING", "name") ;
        businessTableConfig.addFieldConfig("full_address", "STRING", "full_address") ;
        businessTableConfig.addFieldConfig("city", "STRING", "city") ;
        businessTableConfig.addFieldConfig("state", "STRING", "state") ;
        businessTableConfig.addFieldConfig("open", "BOOLEAN", "open") ;
        businessTableConfig.addFieldConfig("longitude", "DOUBLE", "longitude") ;
        businessTableConfig.addFieldConfig("latitude", "DOUBLE", "latitude") ;
        businessTableConfig.addFieldConfig("categories", "STRING", "categories") ;
        businessTableConfig.addFieldConfig("review_count", "INT", "review_count") ;
        businessTableConfig.addFieldConfig("stars", "FLOAT", "stars") ;
        businessTableConfig.addFieldConfig("neighborhoods", "STRING", "neighborhoods") ;
        
        FileImportConfig checkinTableConfig =
            new FileImportConfig("checkin", "create table checkin", "dfs:/user/hive/yelpdb/json/training/checkin/data.json", "Json") ;
        checkinTableConfig.addFieldConfig("business_id", "STRING", "business_id") ;
        checkinTableConfig.addFieldConfig("checkin_info", "STRING", "checkin_info") ;
        
        FileImportConfig reviewTableConfig =
            new FileImportConfig("review", "create table review", "dfs:/user/hive/yelpdb/json/training/review/data.json", "Json") ;
        reviewTableConfig.addFieldConfig("review_id", "STRING", "review_id") ;
        reviewTableConfig.addFieldConfig("business_id", "STRING", "business_id") ;
        reviewTableConfig.addFieldConfig("user_id", "STRING", "user_id") ;
        reviewTableConfig.addFieldConfig("stars", "FLOAT", "stars") ;
        reviewTableConfig.addFieldConfig("text", "STRING", "text") ;
        reviewTableConfig.addFieldConfig("`date`", "STRING", "date") ;
        reviewTableConfig.addFieldConfig("vote_funny", "INT", "votes.funny") ;
        reviewTableConfig.addFieldConfig("vote_useful", "INT", "votes.useful") ;
        reviewTableConfig.addFieldConfig("vote_cool", "INT", "votes.cool") ;
        
        FileImportConfig[] config ={
          userTableConfig, businessTableConfig, checkinTableConfig, reviewTableConfig 
        };
        for(FileImportConfig sel : config) {
          tabbedPane.addTabView(sel.getTable(), new FileImportPanel(sel));
        }
        tabbedPane.setSelectedTab(1) ;
      }
    });
    
    JButton demoTwitter = new JButton("Saarus Twitter");
    demoTwitter.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        int tabCount = tabbedPane.getTabCount() ;
        for(int i = tabCount - 1; i > 0; i--) {
          tabbedPane.removeTabAt(i) ;
        }
        
        FileImportConfig twitterConfig =
          new FileImportConfig("twitter_train", "create table twitter", "dfs:/user/hive/twitter-data/50kSentimentCorpus.csv", "Csv") ;
        twitterConfig.addFieldConfig("sentimenttext", "STRING", "SentimentText") ;
        twitterConfig.addFieldConfig("sentiment", "STRING", "Sentiment") ;
        
        tabbedPane.addTabView(twitterConfig.getTable(), new FileImportPanel(twitterConfig));
        tabbedPane.setSelectedTab(1) ;
      }
    });
    panel.add(demoYelp) ;
    panel.add(demoTwitter) ;
    panel.add(viewScript) ;
    return panel ;
  }
  
  protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs) throws NotConfigurableException {
    System.out.println("dialog load settings from " );
    try {
      FileImportConfigs configs = new FileImportConfigs(settings) ;
      Iterator<FileImportConfig> i = configs.getFileImportConfig().iterator() ;
      while(i.hasNext()) {
        FileImportConfig config = i.next() ;
        tabbedPane.addTabView(config.getTable(), new FileImportPanel(config));
      }
      if(tabbedPane.getTabCount() == 1) {
        FileImportConfig config = new FileImportConfig() ;
        tabbedPane.addTabView(config.getTable(), new FileImportPanel(config));
      }
      tabbedPane.setSelectedTab(1) ;
      System.out.println(configs);
    } catch (InvalidSettingsException e) {
      e.printStackTrace();
    }
  }
  
  public FileImportConfigs getFileImportConfigs() {
    FileImportConfigs configs = new FileImportConfigs() ;
    int tabCount = tabbedPane.getTabCount() ;
    for(int i = 1; i < tabCount; i++) {
      FileImportPanel panel = (FileImportPanel) tabbedPane.getTabAt(i) ;
      configs.addConfig(panel.getFileImportConfig()) ;
    }
    return configs ;
  }

  @Override
  protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
    System.out.println("dialog saveSettingsTo..................");
    FileImportConfigs configs = getFileImportConfigs() ;
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