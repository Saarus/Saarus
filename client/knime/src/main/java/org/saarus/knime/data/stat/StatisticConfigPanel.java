package org.saarus.knime.data.stat;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.table.AbstractTableModel;

import org.saarus.client.HiveClient;
import org.saarus.client.RESTClient;
import org.saarus.knime.ServiceContext;
import org.saarus.knime.data.stat.StatisticConfigs.StatisticConfig;
import org.saarus.service.sql.TableMetadata;
import org.saarus.service.sql.TableMetadata.FieldInfo;
import org.saarus.swing.util.SpringUtilities;

public class StatisticConfigPanel extends JPanel {
  
  private JComboBox<String> tableInput;
  private JTextField descInput ;
  private FieldConfigTable fieldConfigTable ;
  
  public StatisticConfigPanel(StatisticConfig config) {
    setLayout(new BorderLayout()) ;
    try {
      ServiceContext context = ServiceContext.getInstance() ;
      RESTClient restClient = context.getClientContext().getBean(RESTClient.class) ;
      HiveClient hclient = restClient.getHiveClient() ;
      
      add(createInputBox(hclient, config), BorderLayout.NORTH);
      add(createTableMetadataBox(hclient, config), BorderLayout.CENTER) ;
    } catch(Throwable t) {
      t.printStackTrace() ;
    }
  }

  private JPanel createInputBox(final HiveClient hclient, StatisticConfig config) {
    JPanel panel = new JPanel(new SpringLayout()) ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Table"));
    
    List<String> items = new ArrayList<String>() ;
    items.add("Select One") ;
    items.addAll(hclient.listTable(false)) ;
    tableInput = new JComboBox<String>(items.toArray(new String[items.size()]));
    tableInput.setSelectedItem(config.table) ;
    //tableInput.setEditable(true);
    tableInput.setToolTipText("Select the table");
    tableInput.addItemListener(new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED) {
          System.out.println("Item change : " + e.getItem() + " start...");
          String table = e.getItem().toString() ;
          fieldConfigTable.update(hclient, table) ;
          System.out.println("Item change : " + e.getItem() + " done...");
        }
      }
    });
    
    
    panel.add(new JLabel("Table")) ;
    panel.add(tableInput) ;

    descInput = new JTextField("description") ;
    panel.add(new JLabel("Description")) ;
    panel.add(descInput) ;

    SpringUtilities.makeCompactGrid(panel, /*rows, cols*/2, 2,  /*initX, initY*/ 6, 6, /*xPad, yPad*/6, 6);       
    return panel ;
  }

  private JPanel createTableMetadataBox(HiveClient hclient, StatisticConfig config) {
    JPanel panel = new JPanel(new BorderLayout()) ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Select Field"));
    
    fieldConfigTable = new FieldConfigTable(hclient, config.table) ;
    JScrollPane scrollPane = new JScrollPane(fieldConfigTable);
    panel.add(scrollPane, BorderLayout.CENTER);
    return panel ;
  }

  StatisticConfig getStatisticConfig() {
    String table = "";
    if(tableInput.getSelectedItem() != null) {
      table = tableInput.getSelectedItem().toString();
    }
    String desc  = descInput.getText() ;
    
    TableFieldModel tmodel = (TableFieldModel) fieldConfigTable.getModel() ;
    StringBuilder b = new StringBuilder() ;
    b.append("SELECT") ;
    for(int i = 0; i < tmodel.data.length; i++) {
      Object[] row = tmodel.data[i] ;
      String fieldName = row[0].toString() ;
      String fieldType = row[1].toString() ;
      if(i > 0) b.append(", ") ;
      if(fieldType.equalsIgnoreCase("string")) {
        b.append(" string_field_stat(").append(fieldName).append(") AS ").append(fieldName) ;
      } else if(fieldType.equalsIgnoreCase("int") || fieldType.equalsIgnoreCase("float") ||
                fieldType.equalsIgnoreCase("double") || fieldType.equalsIgnoreCase("long")) {
        b.append(" number_field_stat(").append(fieldName).append(") AS ").append(fieldName) ;
      } else {
        b.append(" field_stat(").append(fieldName).append(") AS ").append(fieldName) ;
      }
    }
    b.append(" FROM ").append(table);
    String query = b.toString()  ;
    StatisticConfig config = new StatisticConfig(table, desc, query) ;
    return config ;
  }
  
  static public class FieldConfigTable extends JTable {
    public FieldConfigTable(HiveClient hclient, String tableName) {
      update(hclient, tableName) ;
      addMouseListener(new MouseAdapter() {
        public void mousePressed(MouseEvent e) {
          int selectedRow = FieldConfigTable.this.getSelectedRow();
          int selectedColumn = FieldConfigTable.this.getSelectedColumn();
          String selectedData = (String) FieldConfigTable.this.getValueAt(selectedRow, selectedColumn);
          System.out.println("Selected: " + selectedData);
          if(selectedColumn == 3) {
            AlgorithmSelector dialog = new AlgorithmSelector();  
            dialog.setLocationRelativeTo(FieldConfigTable.this) ;
            dialog.setVisible(true);
          }
        }
      }) ;
    }
    
    void update(HiveClient hclient, String tableName) {
      TableMetadata tmeta = null ;
      if(!"Select One".equals(tableName)) {
        tmeta = hclient.descTable(tableName, false);
      }
      
      TableFieldModel model = new TableFieldModel(tmeta);
      setModel(model) ;
      getColumnModel().getColumn(3).setMinWidth(300);
      model.fireTableDataChanged() ;
    }
  }

  static class TableFieldModel extends AbstractTableModel {
    static String columnNames[] = { "Field Name", "Field Type", "Basic Analytic", "Advanced Analytic"};

    private Object data[][] ;
    
    public TableFieldModel(TableMetadata tmeta) {
      if(tmeta == null) {
        data = new Object[0][0] ;
      } else {
        List<FieldInfo> fholder = tmeta.getFields() ;
        data = new Object[fholder.size()][3] ;
        for(int i = 0; i < data.length; i++) {
          FieldInfo finfo = fholder.get(i) ;
          data[i] = new Object[] {
           finfo.getName(), finfo.getType(), true, "" 
          } ;
        }
      }
    }
    
    public int getColumnCount() { return columnNames.length; }

    public String getColumnName(int column) { return columnNames[column]; }

    public int getRowCount() { return data.length ; }

    public Object getValueAt(int row, int column) { return data[row][column]; }

    public Class getColumnClass(int column) {
      return (getValueAt(0, column).getClass());
    }

    public void setValueAt(Object value, int row, int column) {
      data[row][column] = value;
    }

    public boolean isCellEditable(int row, int column) { return (column != 0); }
  }


  static public class AlgorithmSelector extends JDialog {
    public AlgorithmSelector() {
      setTitle("Select The Algorithms") ;
      setMinimumSize(new Dimension(150, 200)) ;
      setLayout(new BorderLayout()) ;
      setAlwaysOnTop(true) ;
      JButton close = new JButton("OK") ;
      close.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent event) {
          onClose() ;
        }
      }) ;
      JPanel algPanel = new JPanel() ;
      algPanel.setLayout(new SpringLayout()) ;
      String[] algorithms = {"ngram", "minLength", "maxLength", "avgLength"} ;
      for(int i = 0; i < algorithms.length; i++) {
        JCheckBox checkBox = new JCheckBox() ;
        checkBox.setName(algorithms[i]) ;
        algPanel.add(checkBox) ;
        algPanel.add(new JLabel(algorithms[i])) ;
      }
      SpringUtilities.makeCompactGrid(algPanel, /*rows, cols*/algorithms.length, 2,  
                                     /*initX, initY*/ 6, 6, /*xPad, yPad*/6, 6);
      add(algPanel, BorderLayout.CENTER) ;
      add(close, BorderLayout.SOUTH) ;
    }
    
    public void onClose() {
      dispose() ;
    }
  }
}