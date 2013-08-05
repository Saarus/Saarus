package org.saarus.swing.sql;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.Document;

import org.saarus.swing.sql.SQLQuery.JoinClause;
import org.saarus.swing.sql.SQLQuery.JoinTemplate;
import org.saarus.swing.sql.SQLTable.Field;
import org.saarus.swing.util.SpringUtilities;

public class SQLOutputTable extends JPanel implements SQLInputTableListener {
  static String COLUMN_NAMES[] = {"Field", "Type", "Map From",  "Expression"};

  private SQLQuery sqlQuery ;
  
  private ListTableField listField ;
  private QueryPanel queryPanel ;
  private OtherPanel otherPanel ;

  public SQLOutputTable(SQLTable sqlTable) {
    sqlTable.getFields().clear() ;
    this.sqlQuery = new SQLQuery(sqlTable) ;

    setLayout(new SpringLayout()) ;
    add(new TableInfo()) ;

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
    SpringUtilities.makeCompactGrid(this, /*rows, cols*/3, 1,  /*initX, initY*/ 3, 3, /*xPad, yPad*/2, 2); 
  }

  public SQLQuery getSQLQuery() { return this.sqlQuery ; }

  public void onSelectInputTableField(SQLTable table, SQLTable.Field field, boolean select) {
    if(select) sqlQuery.getOutputSQLTable().addField(field) ;
    else sqlQuery.getOutputSQLTable().removeFieldByMapFrom(field) ;
    listField.onSQLQueryChange() ;
  }

  public void onAddInputTable(SQLTable table) {
    sqlQuery.addInputSQLTable(table) ;
    listField.onSQLQueryChange() ;
    queryPanel.onSQLQueryChange() ;
  }

  public void onRemoveInputTable(SQLTable table) {
    sqlQuery.removeInputSQLTable(table) ;
    listField.onSQLQueryChange() ;
    queryPanel.onSQLQueryChange() ;
  }

  public void updateWithInputTable(SQLTable[] inputTable) {
    for(SQLTable sel : inputTable) {
      onAddInputTable(sel) ;
    }
  }

  public class TableInfo extends JPanel {
    JTextField tableName ;
    JCheckBox  newTable ;
    
    public TableInfo() {
      setLayout(new SpringLayout()) ;
      tableName = new JTextField(sqlQuery.getOutputSQLTable().getTableName()) ;
      tableName.getDocument().addDocumentListener(new ChangeTextListener() {
        void onChange(String text) {
          sqlQuery.getOutputSQLTable().setTableName(text) ;
        }
      });
      tableName.setMaximumSize(new Dimension(250, 25)) ;
      add(new JLabel("Table")) ;
      add(tableName) ;
      
      newTable = new JCheckBox() ;
      newTable.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          sqlQuery.setCreateNewOutputTable(newTable.isSelected()) ;
        }
      });
      add(new JLabel("Create New")) ;
      add(newTable) ;
      SpringUtilities.makeCompactGrid(this, /*rows, cols*/1, 4,  /*initX, initY*/ 6, 6, /*xPad, yPad*/6, 6); 
    }
  }
  
  public class ListTableField extends JTable {
    private List<Field> fields ;

    public ListTableField() {
      fields = sqlQuery.getOutputSQLTable().getFields() ;
      final TableModel model = new AbstractTableModel() {
        public Class getColumnClass(int column) {
          switch (column) {
          default: return String.class;
          }
        }

        public String getColumnName(int column) { return COLUMN_NAMES[column]; }

        public int getRowCount() { return fields.size() ;}
        public int getColumnCount() { return COLUMN_NAMES.length ; }

        public Object getValueAt(int rowIndex, int columnIndex) {
          if(columnIndex == 0) return fields.get(rowIndex).getName() ;
          else if(columnIndex == 1) return fields.get(rowIndex).getType() ;
          else if(columnIndex == 2) return fields.get(rowIndex).getMapFromExpression() ;
          else if(columnIndex == 3) return fields.get(rowIndex).getExpression() ;
          return null;
        }

        public boolean isCellEditable(int row, int col) { 
          if(col == 2) return false ;
          else return true ; 
        }

        public void setValueAt(Object aValue, int row, int col) {
          Field field = fields.get(row) ;
          if(col == 0) field.setName((String)aValue) ;
          else if(col == 1) field.setType((String)aValue) ;
          else if(col == 3) field.setExpression((String)aValue) ;
        }
      };
      setModel(model) ;
      getColumnModel().getColumn(0).setPreferredWidth(15) ;
      getColumnModel().getColumn(1).setPreferredWidth(10) ;
      getColumnModel().getColumn(2).setPreferredWidth(15) ;

      createPopupMenu();
    }

    public void onSQLQueryChange() {
      AbstractTableModel model = (AbstractTableModel) getModel() ;
      model.fireTableDataChanged() ;
    }

    private void createPopupMenu() {
      final JPopupMenu popup = new JPopupMenu();
      JMenuItem addField = new JMenuItem("Add Field");
      addField.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          sqlQuery.getOutputSQLTable().addField("", "", null);
          onSQLQueryChange();
        }
      });
      popup.add(addField);

      JMenuItem delField = new JMenuItem("Delete Field");
      popup.add(delField);

      JMenuItem moveUp = new JMenuItem("Move Up");
      popup.add(moveUp);

      JMenuItem moveDown = new JMenuItem("Move Down");
      popup.add(moveDown);

      MouseListener popupListener = new MouseAdapter() {
        public void mouseReleased(MouseEvent e) {
          if(SwingUtilities.isRightMouseButton(e)) {
            popup.show(e.getComponent(), e.getX(), e.getY());
          }
        }
      };
      addMouseListener(popupListener);
    }
    
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
      Component c = super.prepareRenderer(renderer, row, column);
      if (c instanceof JComponent) {
        JComponent jc = (JComponent) c;
        Object val = getValueAt(row, column) ;
        if(val != null) {
          jc.setToolTipText(val.toString());
        }
      }
      return c;
    }
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

  public class OtherPanel extends JPanel {
    public OtherPanel() {
      setLayout(new SpringLayout()) ;
      setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
      addInput("Group By", new JTextField()) ;
      addInput("Limit", new JTextField()) ;
      addInput("Location", new JTextField()) ;
      SpringUtilities.makeCompactGrid(this, /*rows, cols*/3, 2,/*initX, initY*/ 6, 6,  /*xPad, yPad*/ 6, 6);       
    }

    private void addInput(String label, JTextField comp) {
      add(new JLabel(label)) ;
      add(comp) ;
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
        final JTextField condition = new JTextField(joinClause.getCondition()) ;
        condition.getDocument().addDocumentListener(new ChangeTextListener() {
          public void onChange(String text) { 
            joinClause.setCondition(text) ;
          }
        });
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

  static abstract public class ChangeTextListener implements DocumentListener {
    public void changedUpdate(DocumentEvent e) { }
    public void removeUpdate(DocumentEvent e) {
      Document doc = e.getDocument() ;
      try {
        String text = doc.getText(0, doc.getLength()) ;
        onChange(text) ;
      } catch(Exception ex) {
      }
    }

    public void insertUpdate(DocumentEvent e) {
      Document doc = e.getDocument() ;
      try {
        String text = doc.getText(0, doc.getLength()) ;
        onChange(text) ;
      } catch(Exception ex) {
      }
    }

    abstract void onChange(String text) ;
  }
}
