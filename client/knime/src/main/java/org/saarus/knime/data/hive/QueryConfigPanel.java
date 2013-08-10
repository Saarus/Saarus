package org.saarus.knime.data.hive;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import org.saarus.knime.data.hive.QueryConfigs.QueryConfig;
import org.saarus.swing.BeanBindingJTextArea;
import org.saarus.swing.BeanBindingJTextField;
import org.saarus.swing.JTabbedPaneUI;
import org.saarus.swing.SpringLayoutGridJPanel;
import org.saarus.swing.sql.SQLOutputTable;
import org.saarus.swing.sql.SQLQuery;
import org.saarus.swing.sql.SQLQueryBuilderUtil;

public class QueryConfigPanel extends JPanel {
  final static int MAX_WIDTH = QueryNodeDialog.WIDTH ;
  
  private BeanBindingJTextField<QueryConfig> nameInput ;
  private BeanBindingJTextArea<QueryConfig> queryArea ;
  private SQLQueryBuilderDialog queryBuilderDialog ;
  private QueryConfig config ;
  
  public QueryConfigPanel(QueryConfig config) {
    this.config =  config ;
    setLayout(new BorderLayout()) ;
    add(createDescBox(config),       BorderLayout.NORTH);
    add(createQueryBox(config), BorderLayout.CENTER) ;
  }
  
  private JPanel createDescBox(QueryConfig config) {
    SpringLayoutGridJPanel panel = new SpringLayoutGridJPanel() ;
    panel.createBorder("Description");
    nameInput = new BeanBindingJTextField<QueryConfig>(config, "name") {
      public String onTextChange(String text) {
        JTabbedPaneUI tabPanel = 
            (JTabbedPaneUI)SwingUtilities.getAncestorOfClass(JTabbedPaneUI.class, QueryConfigPanel.this) ;
        tabPanel.renameTab(text, QueryConfigPanel.this) ;
        return text ;
      }
    } ;
    panel.addRow("Name", nameInput) ;
    panel.addRow("Description", new BeanBindingJTextField<QueryConfig>(config, "description")) ;
    panel.makeGrid() ;
    return panel ;
  }
  
  private JPanel createQueryBox(QueryConfig config) {
    JPanel panel = new JPanel(new BorderLayout()) ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Query"));
    JToolBar toolbar = new JToolBar() ;
    toolbar.add(new AbstractAction("Design") {
      public void actionPerformed(ActionEvent e) {
        SQLQueryBuilderDialog queryBuilder = getSQLQueryBuilderDialog() ;
        queryBuilder.setLocation(50, 20) ;
        queryBuilder.setVisible(true) ;
      }
    });
    panel.add(toolbar, BorderLayout.NORTH);
    
    queryArea = new BeanBindingJTextArea<QueryConfig>(config, "query") ;
    panel.add(new JScrollPane(queryArea), BorderLayout.CENTER);
    return panel ;
  }
  
  QueryConfig getQueryConfig() { return config ; }
  
  SQLQueryBuilderDialog getSQLQueryBuilderDialog() {
    if(queryBuilderDialog == null) {
      queryBuilderDialog = new SQLQueryBuilderDialog() ;
      queryBuilderDialog.addToolbarAction("Generate SQL", new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
          SQLOutputTable outTable = SQLQueryBuilderUtil.getSQLOutputTable(queryBuilderDialog.getSQLQueryBuilder()) ;
          SQLQuery sqlQuery = outTable.getSQLQuery() ;
          StringBuilder b = new StringBuilder() ;
          if(sqlQuery.getCreateNewOutputTable()) {
            b.append(sqlQuery.getOutputSQLTable().buildDropTableSQL() + ";\n\n");
            b.append(sqlQuery.getOutputSQLTable().buildCreateTable() + ";\n\n");
          }
          b.append(sqlQuery.buildInsertSQLQuery() + ";\n\n");
           
          nameInput.setText(sqlQuery.getOutputSQLTable().getTableName()) ;
          JTabbedPaneUI tabPanel = 
              (JTabbedPaneUI)SwingUtilities.getAncestorOfClass(JTabbedPaneUI.class, QueryConfigPanel.this) ;
          tabPanel.renameTab(nameInput.getText(), QueryConfigPanel.this) ;
          queryArea.setText(b.toString());
          queryBuilderDialog.setVisible(false) ;
        }
      });
    }
    return queryBuilderDialog ;
  }
}