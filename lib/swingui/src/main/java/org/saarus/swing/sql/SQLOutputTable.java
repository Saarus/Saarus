package org.saarus.swing.sql;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;

import org.saarus.swing.BeanBindingJCheckBox;
import org.saarus.swing.BeanBindingJTable;
import org.saarus.swing.BeanBindingJTextField;
import org.saarus.swing.SpringLayoutGridJPanel;
import org.saarus.swing.sql.SQLQuery.JoinClause;
import org.saarus.swing.sql.SQLQuery.JoinTemplate;
import org.saarus.swing.sql.model.SQLTable;
import org.saarus.swing.sql.model.SQLTable.Field;
import org.saarus.swing.util.SpringUtilities;

public class SQLOutputTable extends JPanel implements SQLInputTableListener {
  static String COLUMN_NAMES[]   = {"Field", "Type", "Map From",  "Expression"};
  static String FIELD_PROPERTY[] = {"name", "type", "mapFrom",  "expression"};

  private SQLQuery sqlQuery ;
  
  private ListTableField listField ;
  private QueryPanel queryPanel ;
  private OtherPanel otherPanel ;

  public SQLOutputTable(SQLTable sqlTable) {
    sqlTable.getFields().clear() ;
    this.sqlQuery = new SQLQuery(sqlTable) ;

    setLayout(new SpringLayout()) ;
    add(new SpringLayoutGridJPanel() {{
      BeanBindingJTextField<SQLTable> tableName = 
          new BeanBindingJTextField<SQLTable>(sqlQuery.getOutputSQLTable(), "tableName") ;
      tableName.setMaximumSize(new Dimension(250, 25)) ;
      addRow(
        "Table", tableName,
        "Create New", new BeanBindingJCheckBox<SQLQuery>(sqlQuery, "createNewOutputTable")
      ) ;
      makeGrid() ;
    }}) ;

    listField = new ListTableField() ;
    JScrollPane scroll = new JScrollPane(listField) ;
    scroll.setPreferredSize(new Dimension(250, 100)) ;
    add(scroll) ;

    JTabbedPane tabbedPane = new JTabbedPane();
    queryPanel = new QueryPanel() ;
    tabbedPane.addTab("Query", queryPanel) ;
    otherPanel = new OtherPanel() ;
    tabbedPane.addTab("Other", otherPanel) ;
    add(tabbedPane) ;
    SpringUtilities.makeCompactGrid(this, /*beans, cols*/3, 1,  /*initX, initY*/ 3, 3, /*xPad, yPad*/2, 2); 
  }

  public SQLQuery getSQLQuery() { return this.sqlQuery ; }

  public void onSelectInputTableField(SQLTable table, SQLTable.Field field, boolean select) {
    if(select) sqlQuery.getOutputSQLTable().addField(field) ;
    else sqlQuery.getOutputSQLTable().removeFieldByMapFrom(field) ;
    listField.fireTableDataChanged() ;
  }

  public void onAddInputTable(SQLTable table) {
    sqlQuery.addInputSQLTable(table) ;
    listField.fireTableDataChanged() ;
    queryPanel.onSQLQueryChange() ;
  }

  public void onRemoveInputTable(SQLTable table) {
    sqlQuery.removeInputSQLTable(table) ;
    listField.fireTableDataChanged() ;
    queryPanel.onSQLQueryChange() ;
  }

  public void updateWithInputTable(SQLTable[] inputTable) {
    for(SQLTable sel : inputTable) {
      onAddInputTable(sel) ;
    }
  }
  
  public class ListTableField extends BeanBindingJTable<Field> {
    public ListTableField() {
      super(COLUMN_NAMES, FIELD_PROPERTY, sqlQuery.getOutputSQLTable().getFields()) ;
      createRowPopupMenu() ;
      getColumnModel().getColumn(0).setPreferredWidth(15) ;
      getColumnModel().getColumn(1).setPreferredWidth(10) ;
      getColumnModel().getColumn(2).setPreferredWidth(15) ;
    }

    protected Field newBean() {
      return sqlQuery.getOutputSQLTable().newField() ;
    }
    
    public boolean isBeanEditableAt(int row, int col) { 
      if(col == 2) return false ;
      return true ; 
    }

    public boolean onAddRow() { 
      sqlQuery.getOutputSQLTable().addField("", "", null);
      return true;  
    }
    
    public boolean onRemoveRow(Field bean, int row) { return true ; }
  }

  public class QueryPanel extends JPanel {
    private QueryTemplatePanel body ;

    public QueryPanel() {
      super(new BorderLayout());
      JToolBar toolBar = new JToolBar();
      toolBar.setFloatable(false);
      JButton join = new JButton("Join");
      JButton where = new JButton("Where");
      toolBar.add(join);
      toolBar.add(where);
      add(toolBar, BorderLayout.NORTH) ;

      setClausePanelBody(new JoinTemplatePanel(sqlQuery.createJoinTemplate())) ;
    }

    public void setClausePanelBody(QueryTemplatePanel body) {
      if(this.body != null) remove(this.body) ;
      this.body = body ;
      this.body.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
      add(this.body, BorderLayout.CENTER) ;
    }

    public void onSQLQueryChange() {
      body.onSQLQueryChange() ;
    }
  }

  public class OtherPanel extends SpringLayoutGridJPanel {
    public OtherPanel() {
      createBorder();
      addRow("Group By", new JTextField()) ;
      addRow("Limit", new JTextField()) ;
      addRow("Location", new JTextField()) ;
      makeGrid() ;
    }
  }

  abstract public class QueryTemplatePanel extends JPanel {
    abstract public void onSQLQueryChange() ;
  }

  public class WhereClause extends QueryTemplatePanel {
    public void onSQLQueryChange() {
    }
  }

  public class JoinTemplatePanel extends QueryTemplatePanel {
    private JoinTemplate template ;
    private JTextField fromTable ;

    public JoinTemplatePanel(JoinTemplate template) {
      this.template = template ;
      setLayout(new GridBagLayout());
      fromTable = new JTextField() ;
      onSQLQueryChange(); 
    }

    public void onSQLQueryChange() {
      removeAll() ;
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.HORIZONTAL;
      c.insets = new Insets(5,3,0,0);
      if(template.getFromTableClause() != null) {
        fromTable.setText(template.getFromTableClause().getTable().getTableName()) ;
        addRow(0, c, "FROM", fromTable) ;
      }
      List<JoinClause> joinClauses = template.getJoinClauses() ;
      for(int i = 0; i < joinClauses.size(); i++) {
        final JoinClause joinClause = joinClauses.get(i) ;
        JTextField onTable = new JTextField(joinClause.getOnTable().getTable().getTableName()) ;
        onTable.setEditable(false) ;
        BeanBindingJTextField condition = new BeanBindingJTextField(joinClause, "condition") ;
        addRow(i + 1, c, "JOIN", onTable,  "ON", condition) ;
      }
    }

    private void addRow(int row, GridBagConstraints c, Object ... comp) {
      c.gridy = row ;
      for(int i = 0; i < comp.length; i++) {
        c.gridx = i   ;
        c.gridwidth =  1 ;
        c.weightx   =  0 ;
        if(i == comp.length - 1) {
          c.gridwidth = GridBagConstraints.REMAINDER ;
          c.weightx = 1 ;
        }
        if(comp[i] instanceof String) {
          JLabel label = new JLabel((String) comp[i]) ;
          c.weightx = 0 ;
          add(label, c) ;
        } else {
          add((JComponent)comp[i], c) ;
        }
      }
    }
  }
}
