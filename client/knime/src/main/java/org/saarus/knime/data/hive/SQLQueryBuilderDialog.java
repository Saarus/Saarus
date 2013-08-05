package org.saarus.knime.data.hive;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;

import org.saarus.client.HiveClient;
import org.saarus.client.RESTClient;
import org.saarus.knime.ServiceContext;
import org.saarus.service.sql.TableMetadata;
import org.saarus.swing.sql.SQLQueryBuilder;
import org.saarus.swing.sql.SQLTable;

public class SQLQueryBuilderDialog extends JDialog {
  private static final long serialVersionUID = 1L;
  
  SQLQueryBuilder queryBuilder ;
  
  public SQLQueryBuilderDialog() {
    setTitle("SQL Query Builder") ;
    setLayout(new BorderLayout()) ;
    setMinimumSize(new Dimension(900, 600)) ;
    setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE) ;
    setModalityType(ModalityType.APPLICATION_MODAL) ;
    
    queryBuilder = new SQLQueryBuilder();
    loadTables(getTableMetadata(false)) ;
    
    addToolbarAction("Reload Tables", new AbstractAction() {
      public void actionPerformed(ActionEvent evt) {
        loadTables(getTableMetadata(true)) ;
      }
    });
    add(queryBuilder, BorderLayout.CENTER) ;
  }

  public SQLQueryBuilder getSQLQueryBuilder() { return this.queryBuilder ; }
  
  public void addToolbarAction(String label, Action action) {
    queryBuilder.getToolbar().add(queryBuilder, label, null, action);
  }
  
  private void loadTables(TableMetadata[] tmeta) {
    queryBuilder.getSourcePallete().removeAll() ;
    queryBuilder.getOutputPallete().removeAll() ;
    queryBuilder.addSources(createSQLInputTables(tmeta)) ;
    queryBuilder.addOutput(createSQLOutputTables(tmeta)) ;
  }
  
  SQLTable[] createSQLInputTables(TableMetadata[] tmeta) {
    SQLTable[] sqlTable = new SQLTable[tmeta.length]; 
    for(int i = 0 ; i < tmeta.length; i++) {
      sqlTable[i] = new SQLTable() ;
      sqlTable[i].setTableName(tmeta[i].getTableName()) ;
      sqlTable[i].setType(SQLTable.INPUT_TABLE_TYPE) ;
      for(TableMetadata.FieldInfo finfo : tmeta[i].getFields()) {
        sqlTable[i].addField(finfo.getName(), finfo.getType(), null) ;
      }
    }
    return sqlTable ;
  }
  
  SQLTable[] createSQLOutputTables(TableMetadata[] tmeta) {
    List<SQLTable> holder = new ArrayList<SQLTable>() ;
    SQLTable sqlTable = new SQLTable() ;
    sqlTable.setTableName("New_Table") ;
    sqlTable.setType(SQLTable.OUTPUT_TABLE_TYPE) ;
    holder.add(sqlTable) ;
    for(TableMetadata sel : tmeta) {
      sqlTable = new SQLTable() ;
      sqlTable.setTableName(sel.getTableName()) ;
      sqlTable.setType(SQLTable.OUTPUT_TABLE_TYPE) ;
      holder.add(sqlTable) ;
    }
    return holder.toArray(new SQLTable[holder.size()]) ;
  }
  
  TableMetadata[] getTableMetadata(boolean reload) {
    ServiceContext context = ServiceContext.getInstance() ;
    RESTClient restClient = context.getClientContext().getBean(RESTClient.class) ;
    HiveClient hclient = restClient.getHiveClient() ;
    List<String> listTables = hclient.listTable(reload) ;
    String[] table = listTables.toArray(new String[listTables.size()]) ;
    TableMetadata[] tmeta = hclient.descTables(table, reload) ;
    return tmeta ;
  }
}