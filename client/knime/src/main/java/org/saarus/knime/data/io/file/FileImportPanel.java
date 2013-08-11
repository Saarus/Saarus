package org.saarus.knime.data.io.file;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.codehaus.jackson.JsonNode;
import org.saarus.client.HiveClient;
import org.saarus.client.RESTClient;
import org.saarus.knime.ServiceContext;
import org.saarus.knime.data.io.file.FileImportConfigs.FieldConfig;
import org.saarus.knime.data.io.file.FileImportConfigs.FileImportConfig;
import org.saarus.knime.uicomp.JTreeDFSFileSelector;
import org.saarus.service.hadoop.dfs.DFSFile;
import org.saarus.service.hadoop.util.FSResource;
import org.saarus.service.sql.TableMetadata;
import org.saarus.service.util.JSONReader;
import org.saarus.swing.BeanBindingJCheckBox;
import org.saarus.swing.BeanBindingJComboBox;
import org.saarus.swing.BeanBindingJTable;
import org.saarus.swing.BeanBindingJTextField;
import org.saarus.swing.JInfoDialog;
import org.saarus.swing.JTabbedPaneUI;
import org.saarus.swing.SpringLayoutGridJPanel;
import org.saarus.swing.listener.JTextFieldChangeTextListener;
import org.saarus.swing.sql.model.SQLTable.Field;
import org.saarus.util.json.JSONSerializer;

public class FileImportPanel extends JPanel {
  final static int MAX_WIDTH = FileImportNodeDialog.WIDTH ;
  
  private FileImportConfig config ;
  
  private BeanBindingJComboBox<FileImportConfig, String> importType ;
  private BeanBindingJTextField<FileImportConfig> urlInput;
  private SQLTableConfig tableConfig ;
  private FileDataSample dataSample = new FileDataSample();
  
  public FileImportPanel(FileImportConfig config) {
    this.config = config ;
    setLayout(new BorderLayout()) ;
    add(createInputBox(),       BorderLayout.NORTH);
    add(createPreviewDataBox(), BorderLayout.CENTER) ;
  }
  
  private JPanel createInputBox() {
    SpringLayoutGridJPanel panel = new SpringLayoutGridJPanel() ;
    panel.createBorder("Import(press 'Enter' to update preview)") ;
    
    panel.addRow("Description", new BeanBindingJTextField<FileImportConfig>(config, "description")) ;
    
    //url input
    JPanel urlButtonPanel = new JPanel();
    JButton dfsBrowse = new JButton("DFS Browse");
    dfsBrowse.addActionListener(new ActionListener() {
      public void actionPerformed(final ActionEvent e) {
        try {
          JSONFileSelector selector = new JSONFileSelector();
          selector.setSize(new Dimension(300, 500)) ;
          selector.setLocationRelativeTo(FileImportPanel.this) ;
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
        String urlLoc = config.getFile() ;
        String type =  config.getImportType() ;
        dataSample.load(urlLoc, type) ;
        
        JInfoDialog dialog = JInfoDialog.getInstance() ;
        dialog.setSize(new Dimension(400, 500)) ;
        dialog.setInfo(dataSample.getDisplayText()) ;
        dialog.setLocationRelativeTo(FileImportPanel.this) ;
        dialog.setVisible(true) ;
        System.out.println("end preview action") ;
      }
    });
    urlButtonPanel.add(preview) ;
    
    urlInput = new BeanBindingJTextField<FileImportConfig>(config, "file") {
      public String onTextChange(String text) {
        return text.trim() ;
      }
    };
    urlInput.setToolTipText("Enter an URL or browse");
    
    JPanel urlPanel = new JPanel(new BorderLayout());
    urlPanel.add(urlInput, BorderLayout.CENTER) ;
    urlPanel.add(urlButtonPanel, BorderLayout.EAST) ;
    
    panel.addRow("File", urlPanel) ;
    
    importType = new BeanBindingJComboBox<FileImportConfig, String>(config, "importType", new String[] {"Json", "Csv"}) ;
    panel.addRow("Import Type", importType) ;
    
    BeanBindingJTextField<FileImportConfig> tableField = new BeanBindingJTextField<FileImportConfig>(config, "table") ;
    tableField.getDocument().addDocumentListener(new JTextFieldChangeTextListener() {
      public void onChange(String text) {
        JTabbedPaneUI tabPanel = 
            (JTabbedPaneUI)SwingUtilities.getAncestorOfClass(JTabbedPaneUI.class, FileImportPanel.this) ;
        tabPanel.renameTab(text, FileImportPanel.this) ;
      }
    });
    panel.addRow("Table", tableField) ;
    
    panel.addRow("Create New", new BeanBindingJCheckBox<FileImportConfig>(config, "createNew")) ;
    panel.makeGrid() ;
    return panel ;
  }
  
  private JPanel createPreviewDataBox() {
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
          TableMetadata tableMetadata = hclient.descTable(config.getTable(), false) ;
          dataSample.load(config.getFile(), config.getImportType()) ;
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
  
  FileImportConfig getFileImportConfig() { return config ; }
  
  class JSONFileSelector extends JTreeDFSFileSelector {
    private static final long serialVersionUID = 1L;
    
    public JSONFileSelector() throws Exception { super() ; }
    
    public void onClickOK(DFSFile selectFile) {
      if(selectFile != null) {
        String path = selectFile.getPath() ;
        urlInput.setBeanValue(path) ;
        if(path.endsWith(".json")) {
          importType.updateBeanValue("Json") ;
        } else if(path.endsWith(".csv")) {
          importType.updateBeanValue("Csv") ;
        }
      }
      dispose() ;
    }
  }
  
  static public class SQLTableConfig extends  BeanBindingJTable<FieldConfig> {
    static String[] COLUMN_NAMES = {"Field", "Type", "Map Property"} ;
    static String[] BEAN_PROPERTY = {"fieldName", "type", "mapProperty"} ;
    
    FileImportConfig config ;
    
    public SQLTableConfig(FileImportConfig config) {
      this.config = config ;
      init(COLUMN_NAMES, BEAN_PROPERTY, config.getFieldConfigs()) ;
      createRowPopupMenu() ;
    }

    protected boolean isBeanEditableAt(int row, int col) {
      return true;
    }

    protected FieldConfig newBean() { return new FieldConfig(); }
    
    public boolean onAddRow() { 
      beans.add(new FieldConfig()) ;
      return true;  
    }
    
    public boolean onRemoveRow(FieldConfig bean, int row) { 
      beans.remove(row) ;
      return true ; 
    }
    
    public void update(String[][]  mappingData) {
      beans.clear() ;
      for(int i = 0; i < mappingData.length; i++) {
        beans.add(new FieldConfig(mappingData[i][0], mappingData[i][1], mappingData[i][2])) ;
      }
      fireTableDataChanged() ;
    }
    
    public String[][] getFielMappingConfig() {
      String[][] mappingConfigs = new String[beans.size()][] ;
      for(int i = 0; i < mappingConfigs.length; i++) {
        mappingConfigs[i] = beans.get(i).cellValues() ;
      }
      return mappingConfigs ;
    }
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
        JSONReader reader = new JSONReader(new ByteArrayInputStream(sampleData.getBytes())) ;
        JsonNode node = reader.read() ;
        reader.close() ;
        return TableMetadata.autoDetectMapping(mdata, node) ;
      } catch (Exception e) {
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