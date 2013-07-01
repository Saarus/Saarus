package org.saarus.knime.data.in.json;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;

import org.saarus.knime.data.in.json.JSONImportConfigs.JSONImportConfig;
import org.saarus.knime.uicomp.JTreeDFSFileSelector;
import org.saarus.knime.uicomp.SpringUtilities;
import org.saarus.service.hadoop.dfs.DFSFile;

public class JSONImportFileJPanel extends JPanel {
  private static final int HORIZ_SPACE = 10;
  final static int MAX_WIDTH = JSONImportNodeDialog.WIDTH ;
  
  private URLInput urlInput;
  private JTextField descInput, tableInput ;
  private JCheckBox   newTable ;
  private PreviewDataTable previewTable ;
  
  public JSONImportFileJPanel(JSONImportConfig config) {
    setLayout(new BorderLayout()) ;
    add(createInputBox(config),       BorderLayout.NORTH);
    add(createPreviewDataBox(), BorderLayout.CENTER) ;
  }
  
  private JPanel createInputBox(JSONImportConfig config) {
    JPanel panel = new JPanel(new SpringLayout()) ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Import(press 'Enter' to update preview)"));
    
    JButton browse = new JButton("Browse");
    browse.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        try {
          JSONFileSelector selector = new JSONFileSelector();
          selector.setSize(new Dimension(300, 500)) ;
          selector.setLocationRelativeTo(JSONImportFileJPanel.this) ;
          selector.setVisible(true) ;
        } catch (Exception e1) {
          e1.printStackTrace();
        }
      }
    });

    urlInput = new URLInput();
    urlInput.setSelectedItem(config.getPath()) ;
    
    descInput = new JTextField(config.getDescription()) ;
    tableInput = new JTextField(config.getTable()) ;
    newTable = new JCheckBox() ;
    
    panel.add(new JLabel("Description")) ;
    panel.add(descInput) ;
    
    panel.add(new JLabel("Table")) ;
    panel.add(tableInput) ;
    
    panel.add(new JLabel("Create New")) ;
    panel.add(newTable) ;
    
    panel.add(browse) ;
    panel.add(urlInput) ;
    SpringUtilities.makeCompactGrid(panel, /*rows, cols*/4, 2,  /*initX, initY*/ 6, 6, /*xPad, yPad*/6, 6);       
    return panel ;
  }
  
  private JPanel createPreviewDataBox() {
    JPanel panel = new JPanel(new BorderLayout()) ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Preview Data"));
    previewTable = new PreviewDataTable() ;
    JScrollPane scrollPane = new JScrollPane(previewTable);
    //Add the scroll pane to this panel.
    panel.add(scrollPane, BorderLayout.CENTER);
    return panel ;
  }
  
  JSONImportConfig getJSONImportConfig() {
    String table = tableInput.getText() ;
    String desc  = descInput.getText() ;
    String loc = urlInput.getURLLocation() ;
    JSONImportConfig config = new JSONImportConfig(table, desc, loc) ;
    return config ;
  }
  
  class JSONFileSelector extends JTreeDFSFileSelector {
    private static final long serialVersionUID = 1L;
    
    public JSONFileSelector() throws Exception { super() ; }
    
    public void onClickOK(DFSFile selectFile) {
      if(selectFile != null) {
        urlInput.setSelectedItem(selectFile.getPath());
        previewTable.setData(null) ;
      }
      dispose() ;
    }
  }
  
  static class URLInput extends JComboBox<String> {
    public URLInput() {
      setEditable(true);
      setToolTipText("Enter an URL of an ASCII data file, select from recent files, or browse");
      addItemListener(new URLFileItemListener());
      addFocusListener(new FocusAdapter() {
        @Override
        public void focusLost(final FocusEvent e) {
        }
      }); 
    }
    
    public String getURLLocation() {
      Object val = getSelectedItem() ;
      if(val == null) return "" ;
      return (String) val ;
    }
  }
  
  static public class PreviewDataTable extends JTable {
    static String[] HEADERS = { "#", "JSON" };
    static Object[][] EMPTY_DATA = { };
    static Object[][] TEST_DATA = {
      { "1", "{'type': 'user, 'user_id': 'CR2y7yEm4X035ZMzrTtN9Q','table: 'Jim','votes': { 'funny': 0, 'useful': 7, 'cool': 0 },'average_stars': 5.0, 'review_count':  6}" },
      { "2", "{'type': 'user, 'user_id': 'CR2y7yEm4X035ZMzrTtN9Q','table: 'Jim','votes': { 'funny': 0, 'useful': 7, 'cool': 0 },'average_stars': 5.0, 'review_count':  6}" },
      { "3", "{'type': 'user, 'user_id': 'CR2y7yEm4X035ZMzrTtN9Q','table: 'Jim','votes': { 'funny': 0, 'useful': 7, 'cool': 0 },'average_stars': 5.0, 'review_count':  6}" },
      { "4", "{'type': 'user, 'user_id': 'CR2y7yEm4X035ZMzrTtN9Q','table: 'Jim','votes': { 'funny': 0, 'useful': 7, 'cool': 0 },'average_stars': 5.0, 'review_count':  6}" },
      { "5", "{'type': 'user, 'user_id': 'CR2y7yEm4X035ZMzrTtN9Q','table: 'Jim','votes': { 'funny': 0, 'useful': 7, 'cool': 0 },'average_stars': 5.0, 'review_count':  6}" }
    };
    
    public PreviewDataTable() {
      DefaultTableModel model = new DefaultTableModel(EMPTY_DATA, HEADERS) ;
      setModel(model) ;
      getColumnModel().getColumn(0).setMaxWidth(30);
    }
    
    public void setData(Object[][] data) {
      DefaultTableModel model = (DefaultTableModel) this.getModel()  ;
      model.setDataVector(TEST_DATA, HEADERS) ;
    }
  }
  
  static public class URLFileItemListener implements ItemListener {
    public void itemStateChanged(ItemEvent event) {
    }
  }
}