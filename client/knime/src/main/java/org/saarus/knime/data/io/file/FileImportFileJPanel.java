package org.saarus.knime.data.io.file;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
import javax.swing.table.AbstractTableModel;

import org.codehaus.jackson.JsonNode;
import org.saarus.client.HiveClient;
import org.saarus.client.RESTClient;
import org.saarus.knime.ServiceContext;
import org.saarus.knime.data.io.file.FileImportConfigs.FileImportConfig;
import org.saarus.knime.uicomp.JInfoDialog;
import org.saarus.knime.uicomp.JTreeDFSFileSelector;
import org.saarus.knime.uicomp.SpringUtilities;
import org.saarus.service.hadoop.dfs.DFSFile;
import org.saarus.service.hadoop.util.FSResource;
import org.saarus.service.sql.TableMetadata;
import org.saarus.service.util.JSONReader;
import org.saarus.util.json.JSONSerializer;

public class FileImportFileJPanel extends JPanel {
  final static int MAX_WIDTH = FileImportNodeDialog.WIDTH ;
  
  private JTextField descInput, tableInput ;
  private URLInput urlInput;
  private JComboBox<String> importType ;
  private JCheckBox   newTable ;
  private SQLTableConfig tableConfig ;
  private FileDataSample dataSample = new FileDataSample();
  
  public FileImportFileJPanel(FileImportConfig config) {
    setLayout(new BorderLayout()) ;
    add(createInputBox(config),       BorderLayout.NORTH);
    add(createPreviewDataBox(config), BorderLayout.CENTER) ;
  }
  
  private JPanel createInputBox(FileImportConfig config) {
    JPanel panel = new JPanel(new SpringLayout()) ;
    String title = "Import(press 'Enter' to update preview)" ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), title));
    
    descInput = new JTextField(config.getDescription()) ;
    panel.add(new JLabel("Description")) ;
    panel.add(descInput) ;
    
    //url input
    JPanel urlButtonPanel = new JPanel();
    
    
    JButton dfsBrowse = new JButton("DFS Browse");
    dfsBrowse.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        try {
          JSONFileSelector selector = new JSONFileSelector();
          selector.setSize(new Dimension(300, 500)) ;
          selector.setLocationRelativeTo(FileImportFileJPanel.this) ;
          selector.setVisible(true) ;
        } catch (Exception e1) {
          e1.printStackTrace();
        }
      }
    });
    urlButtonPanel.add(dfsBrowse) ;
    
    JButton preview = new JButton("Preview");
    preview.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        String urlLoc = urlInput.getURLLocation() ;
        String type =  importType.getSelectedItem().toString() ;
        dataSample.load(urlLoc, type) ;
        
        JInfoDialog dialog = JInfoDialog.getInstance() ;
        dialog.setSize(new Dimension(400, 500)) ;
        dialog.setInfo(dataSample.getDisplayText()) ;
        dialog.setLocationRelativeTo(FileImportFileJPanel.this) ;
        dialog.setVisible(true) ;
        System.out.println("end preview action") ;
      }
    });
    urlButtonPanel.add(preview) ;
    
    urlInput = new URLInput();
    urlInput.setSelectedItem(config.getFile()) ;
    JPanel urlPanel = new JPanel(new BorderLayout());
    urlPanel.add(urlInput, BorderLayout.CENTER) ;
    urlPanel.add(urlButtonPanel, BorderLayout.EAST) ;
    panel.add(new JLabel("File")) ;
    panel.add(urlPanel) ;
    
    importType = new JComboBox<String>(new String[] {"Json", "Csv"}) ;
    importType.setEditable(false);
    importType.setSelectedItem(config.getImportType()) ;
    
    panel.add(new JLabel("Import Type")) ;
    panel.add(importType) ;
    
    tableInput = new JTextField(config.getTable()) ;
    panel.add(new JLabel("Table")) ;
    panel.add(tableInput) ;
    
    newTable = new JCheckBox() ;
    panel.add(new JLabel("Create New")) ;
    panel.add(newTable) ;
    
    SpringUtilities.makeCompactGrid(panel, /*rows, cols*/5, 2,  /*initX, initY*/ 6, 6, /*xPad, yPad*/6, 6);       
    return panel ;
  }
  
  private JPanel createPreviewDataBox(FileImportConfig config) {
    JPanel panel = new JPanel(new BorderLayout()) ;
    panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Table Config"));
    
    tableConfig = new SQLTableConfig(config) ;
    panel.add(new JScrollPane(tableConfig), BorderLayout.CENTER);
    JButton autoDetect = new JButton("Auto Detect");
    autoDetect.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        try {
          ServiceContext context = ServiceContext.getInstance() ;
          RESTClient restClient = context.getClientContext().getBean(RESTClient.class) ;
          HiveClient hclient = restClient.getHiveClient() ;
          TableMetadata tableMetadata = hclient.descTable(tableInput.getText(), false) ;
          dataSample.load(urlInput.getURLLocation(), importType.getSelectedItem().toString()) ;
          String[][] mappingData = dataSample.autoDetectMapping(tableMetadata) ;
          tableConfig.update(mappingData) ;
        } catch(Throwable t) {
          t.printStackTrace() ;
        }
      }
    });
    JPanel buttonPanel = new JPanel(new FlowLayout()) ;
    buttonPanel.add(autoDetect) ;
    panel.add(buttonPanel, BorderLayout.SOUTH) ;
    return panel ;
  }
  
  FileImportConfig getJSONImportConfig() {
    String table = tableInput.getText() ;
    String desc  = descInput.getText() ;
    String loc = urlInput.getURLLocation() ;
    String type = importType.getSelectedItem().toString() ; 
    FileImportConfig config = new FileImportConfig(table, desc, loc, type) ;
    String[][] fieldMappingConfig = tableConfig.getFielMappingConfig() ;
    for(int i = 0; i < fieldMappingConfig.length; i++) {
      config.addFieldConfig(fieldMappingConfig[i][0], fieldMappingConfig[i][1], fieldMappingConfig[i][2]) ;
    }
    return config ;
  }
  
  class JSONFileSelector extends JTreeDFSFileSelector {
    private static final long serialVersionUID = 1L;
    
    public JSONFileSelector() throws Exception { super() ; }
    
    public void onClickOK(DFSFile selectFile) {
      if(selectFile != null) {
        String path = selectFile.getPath() ;
        urlInput.setSelectedItem(path);
        if(path.endsWith(".json")) {
          importType.setSelectedItem("Json") ;
        } else if(path.endsWith(".csv")) {
          importType.setSelectedItem("Csv") ;
        }
      }
      dispose() ;
    }
  }
  
  static class URLInput extends JComboBox<String> {
    public URLInput() {
      setEditable(true);
      setPreferredSize(new Dimension(160, 15));
      setToolTipText("Enter an URL of an ASCII data file, select from recent files, or browse");
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
  
  static public class SQLTableConfig extends JTable {
    public SQLTableConfig(FileImportConfig config) {
      TableFieldModel model = new TableFieldModel(config.getFieldMappingConfig());
      setModel(model) ;
      getColumnModel().getColumn(2).setMinWidth(300);
    }
    
    void update(String[][] mappingData) {
      TableFieldModel model = (TableFieldModel)getModel() ;
      model.data = mappingData ;
      model.fireTableDataChanged() ;
    }
    
    String[][] getFielMappingConfig() {
      List<String[]> holder = new ArrayList<String[]>() ;
      TableFieldModel model = (TableFieldModel) getModel() ;
      if(model.data != null) {
        for(int i = 0; i < model.data.length; i++) {
          String cell0 = this.getValueAt(i, 0).toString() ;
          if(cell0 != null && cell0.length() > 0) {
            String cell1 = getValueAt(i, 1).toString() ;
            if(cell1.length() == 0) cell1 = "?" ;
            String cell2 = getValueAt(i, 2).toString() ;
            if(cell2.length() == 0) cell2 = "?" ;
            holder.add(new String[] {cell0, cell1, cell2}) ;
          }
        }
      }
      return holder.toArray(new String[holder.size()][]) ;
    }
  }

  static class TableFieldModel extends AbstractTableModel {
    static String columnNames[] = { "Field Name", "Field Type", "Data Property"};

    String data[][] ;
    
    public TableFieldModel(String[][] data) {
      if(data == null || data.length == 0) {
        this.data = new String[20][3] ;
      } else {
        this.data = new String[data.length + 10][3] ;
        for(int i = 0; i < data.length; i++) {
          this.data[i] = data[i] ;
        }
      }
    }
    
    public int getColumnCount() { return columnNames.length; }

    public String getColumnName(int column) { return columnNames[column]; }

    public int getRowCount() { return data.length ; }

    public Object getValueAt(int row, int column) { 
      if(data[row][column] == null) return "" ;
      return data[row][column]; 
    }

    public Class<String> getColumnClass(int column) { return String.class; }

    public void setValueAt(Object value, int row, int column) {
      System.out.println("call set value " + value);
      data[row][column] = value.toString();
    }

    public boolean isCellEditable(int row, int column) { return true ; }
  }
  
  static class FileDataSample {
    String fileLoc ;
    String type ;
    String sampleData ;
    Exception error ;
    
    public void load(String fileLoc, String type) {
      if(fileLoc.equals(this.fileLoc) && this.type.equals(type)) return ;
      this.fileLoc = fileLoc ;
      this.type = type ;
      error = null ;
      sampleData = null ;
      try {
        if("Json".equals(type)) {
          this.sampleData = readJsonSample(fileLoc) ;
        } else {
          this.sampleData = readCSVSample(fileLoc) ;
        }
      } catch(Exception ex) {
        this.error = ex ;
        ex.printStackTrace() ;
      }
    }
    
    public String getDisplayText() {
      if(error != null) return error.getMessage() ;
      return sampleData; 
    }
    
    public String[][] autoDetectMapping(TableMetadata mdata) {
      if("Json".equals(type)) {
        return autoDetectJSONMapping(mdata) ;
      } else {
        return autoDetectCSVMapping(mdata) ;
      }
    }
    
    public String[][] autoDetectJSONMapping(TableMetadata mdata) {
      try {
        JsonNode node = JSONSerializer.JSON_SERIALIZER.fromString(sampleData) ;
        node.isValueNode() ;
        return TableMetadata.autoDetectMapping(mdata, node) ;
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null ;
    }
    
    public String[][] autoDetectCSVMapping(TableMetadata mdata) {
      String data = sampleData ;
      if(data == null) data = "" ;
      String[] line = data.split("\n") ;
      String[] header = {} ;
      if(line.length > 0 ) header = line[0].split(",") ;
      return TableMetadata.autoDetectMapping(mdata, header) ;
    }
    
    private String readJsonSample(String loc) throws Exception {
      FSResource res = FSResource.get(fileLoc) ;
      JSONReader reader = new JSONReader(res.getInputStream()) ;
      JsonNode node = reader.read() ;
      reader.close() ;
      return JSONSerializer.JSON_SERIALIZER.toString(node) ;
    }
    
    private String readCSVSample(String loc) throws Exception {
      FSResource res = FSResource.get(fileLoc) ;
      BufferedReader reader = new BufferedReader(new InputStreamReader(res.getInputStream())) ;
      StringBuilder b = new StringBuilder() ;
      String line  = null ;
      int count  = 0 ;
      while(count < 16 && (line = reader.readLine()) != null) {
        if(count > 0) b.append("\n") ;
        b.append(line.trim()) ;
        count++ ;
      }
      return b.toString() ;
    }
  }
}