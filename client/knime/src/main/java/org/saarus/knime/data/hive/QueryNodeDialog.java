package org.saarus.knime.data.hive;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.eclipse.core.runtime.Platform;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.osgi.framework.Bundle;
import org.saarus.knime.data.hive.QueryConfigs.QueryConfig;
import org.saarus.knime.uicomp.JInfoDialog;
import org.saarus.knime.uicomp.JTabbedPaneUI;
import org.saarus.util.IOUtil;
import org.saarus.util.json.JSONSerializer;
/**
 * @author Tuan Nguyen
 */
public class QueryNodeDialog extends NodeDialogPane {
  final static public int WIDTH  = 750 ;
  final static public int HEIGHT = 500 ;
  private JTabbedPaneUI tabbedPane ;
  
  protected QueryNodeDialog() {
    getPanel().setPreferredSize(new Dimension(WIDTH, HEIGHT)) ;

    tabbedPane = new JTabbedPaneUI();
    tabbedPane.addAddButton(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        QueryConfig config = new QueryConfig("Query "  + tabbedPane.getTabCount(), "", "") ;
        tabbedPane.addTabView(config.name, new QueryConfigPanel(config));
      }
    }) ;
    JPanel panel = new JPanel(new BorderLayout()) ;
    panel.add(tabbedPane, BorderLayout.CENTER) ;
    panel.add(createToolBox(), BorderLayout.SOUTH) ;
    addTab("Queries", panel);
  }
  
  private JPanel createToolBox() {
    JPanel panel = new JPanel(new FlowLayout()) ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Tools"));
    JButton viewScript = new JButton("View Script");
    viewScript.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        JInfoDialog dialog = JInfoDialog.getInstance() ;
        try {
          String json = JSONSerializer.JSON_SERIALIZER.toString(getQueryConfigs().getGeneratedTask()) ;
          dialog.setInfo(json) ;
        } catch(Exception ex) {
          ex.printStackTrace() ;
        }
        dialog.setVisible(true) ;
      }
    });
    
    JButton demoYelp = new JButton("Saarus Work Flow Transform");
    demoYelp.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        int tabCount = tabbedPane.getTabCount() ;
        for(int i = tabCount - 1; i > 0; i--) {
          tabbedPane.removeTabAt(i) ;
        }
        Map<String, String> queries = getYelpDemoQueries("resources/yelp.sql");
        for(Map.Entry<String, String> entry : queries.entrySet()) {
          String name = entry.getKey() ;
          String query = entry.getValue() ;
          QueryConfig config = new QueryConfig(name, name, query) ;
          tabbedPane.addTabView(config.name, new QueryConfigPanel(config));
        }
        tabbedPane.setSelectedTab(1) ;
      }
    });
    JButton demoYelpFeatures = new JButton("Saarus Work Flow Features");
    demoYelpFeatures.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        int tabCount = tabbedPane.getTabCount() ;
        for(int i = tabCount - 1; i > 0; i--) {
          tabbedPane.removeTabAt(i) ;
        }
        Map<String, String> queries = getYelpDemoQueries("resources/yelp_features.sql");
        for(Map.Entry<String, String> entry : queries.entrySet()) {
          String name = entry.getKey() ;
          String query = entry.getValue() ;
          QueryConfig config = new QueryConfig(name, name, query) ;
          tabbedPane.addTabView(config.name, new QueryConfigPanel(config));
        }
        tabbedPane.setSelectedTab(1) ;
      }
    });
    panel.add(demoYelp) ;
    panel.add(demoYelpFeatures) ;
    panel.add(viewScript) ;
    return panel ;
  }

  public QueryConfigs getQueryConfigs() {
    QueryConfigs configs = new QueryConfigs() ;
    int tabCount = tabbedPane.getTabCount() ;
    for(int i = 1; i < tabCount; i++) {
      QueryConfigPanel panel = (QueryConfigPanel) tabbedPane.getTabAt(i) ;
      configs.add(panel.getQueryConfig()) ;
    }
    return configs ;
  }
  
  protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs) throws NotConfigurableException {
    System.out.println("dialog load settings from " );
    try {
      QueryConfigs configs = new QueryConfigs(settings) ;
      Iterator<QueryConfig> i = configs.getConfigs().iterator() ;
      while(i.hasNext()) {
        QueryConfig config = i.next() ;
        tabbedPane.addTabView(config.name, new QueryConfigPanel(config));
      }
      if(tabbedPane.getTabCount() == 1) {
        QueryConfig config = new QueryConfig() ;
        tabbedPane.addTabView(config.name, new QueryConfigPanel(config));
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
    QueryConfigs configs = getQueryConfigs() ;
    configs.saveSettings(settings) ;
    System.out.println("dialog saveSettingsTo.................. done");
  }

  
  public void onClose() {
    System.out.println("-----------------------close diaglog-------------------------") ;
  }

  public void onOpen() {
    System.out.println("-----------------------open dialog--------------------------") ;
  }
  
  private Map<String, String> getYelpDemoQueries(String resFile) {
    Map<String, String> holder = new LinkedHashMap<String, String>() ;
    try {
      Bundle bundle = Platform.getBundle("saarus.client.knime");
      URL url = bundle.getEntry(resFile);
      String content = IOUtil.getStreamContentAsString(url.openStream(), "UTF-8") ;
      String[] line = content.split("\n") ;
      int idx =  0 ;
      while(idx < line.length) {
        if(line[idx].startsWith("--")) {
          String name = line[idx].substring(2) ;
          StringBuilder b = new StringBuilder() ;
          idx++ ;
          while(idx < line.length) {
            if(line[idx].startsWith("--")) break ;
            b.append(line[idx]).append("\n") ;
            idx++ ;
          }
          holder.put(name, b.toString()) ;
        }
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return holder ;
  }
}