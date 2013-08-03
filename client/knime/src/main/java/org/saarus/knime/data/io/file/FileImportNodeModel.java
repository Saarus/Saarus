package org.saarus.knime.data.io.file;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.saarus.client.ClientContext;
import org.saarus.client.HiveClient;
import org.saarus.client.RESTClient;
import org.saarus.knime.ServiceContext;
import org.saarus.knime.data.io.file.FileImportConfigs.FileImportConfig;
import org.saarus.service.sql.TableMetadata;
import org.saarus.service.task.Task;
import org.saarus.service.task.TaskResult;
import org.saarus.service.util.JSONSerializer;
/**
 * @author Tuan Nguyen
 */
public class FileImportNodeModel extends NodeModel {
  private static final NodeLogger logger = NodeLogger.getLogger(FileImportNodeModel.class);

  private FileImportConfigs currentSettings = new FileImportConfigs();

  protected FileImportNodeModel() {
    super(0, 1);
  }

  @Override
  protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec) throws Exception {
    try {
      System.out.println("call execute(update)..................");
      ClientContext context = ServiceContext.getInstance().getClientContext() ;
      RESTClient restClient = context.getBean(RESTClient.class) ;
      Task task = currentSettings.getGeneratedTask() ;
      TaskResult taskResult = restClient.submitTask(task) ;
      while(!taskResult.isFinished()) {
        Thread.sleep(1000) ;
        try {
          exec.checkCanceled();
          taskResult = restClient.pollTask(taskResult.getTask()) ;
          exec.setProgress(taskResult.getProgress(), "progress = " + (taskResult.getProgress() * 100) + "%" );
        } catch(CanceledExecutionException cancelEx) {
          System.out.println("catch CanceledExecutionException...") ;
          throw cancelEx ;
        } catch(Throwable t) {
          t.printStackTrace() ;
          throw new CanceledExecutionException(t.getMessage()) ;
        }
      }
      
      
      DataColumnSpec[] allColSpecs = new DataColumnSpec[4];
      allColSpecs[0] = new DataColumnSpecCreator("Table", StringCell.TYPE).createSpec();
      allColSpecs[1] = new DataColumnSpecCreator("Description", StringCell.TYPE).createSpec();
      allColSpecs[2] = new DataColumnSpecCreator("Path", StringCell.TYPE).createSpec();
      allColSpecs[3] = new DataColumnSpecCreator("Table Metadata", StringCell.TYPE).createSpec();
      DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
      BufferedDataContainer container = exec.createDataContainer(outputSpec);

      Iterator<FileImportConfig> i = currentSettings.getFileImportConfig().iterator(); 
      int count = 0 ;
      HiveClient hiveClient = restClient.getHiveClient() ;
      while(i.hasNext()) {
        FileImportConfig config = i.next() ;
        TableMetadata tmeta = hiveClient.descTable(config.getTable(), false) ;
        DataCell[] cells = { 
            new StringCell(config.getTable()), new StringCell(config.getDescription()),
            new StringCell(config.getFile()), new StringCell(JSONSerializer.JSON_SERIALIZER.toString(tmeta))
        } ;
        container.addRowToTable(new DefaultRow(new RowKey("Row " + count), cells));
        count++ ;
      }
      // once we are done, we close the container and return its table
      container.close();
      BufferedDataTable out = container.getTable();
      return new BufferedDataTable[]{out};
    } catch(Exception ex) {
      ex.printStackTrace() ;
      throw ex ;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void reset() {
    currentSettings = new FileImportConfigs() ;
    System.out.println("Call reset..........................");
  }

  /** {@inheritDoc} */
  @Override
  protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
    System.out.println("Call configure(Update)");
    return new DataTableSpec[]{null};
  }

  /** {@inheritDoc} */
  @Override
  protected void saveSettingsTo(final NodeSettingsWO settings) {
    currentSettings.saveSettings(settings) ;
    System.out.println("Call saveSettingsTo...........................");
  }

  /** {@inheritDoc} */
  @Override
  protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
    this.currentSettings.merge(new FileImportConfigs(settings)) ;
    System.out.println("Load loadValidatedSettings(merge)") ;
    System.out.println(this.currentSettings) ;
  }

  /** {@inheritDoc} */
  @Override
  protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
    System.out.println("Call validateSettings...........................");
  }

  /** {@inheritDoc} */
  @Override
  protected void loadInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException {
    System.out.println("Call loadInternals...........................");
  }

  /** {@inheritDoc} */
  @Override
  protected void saveInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException {
    System.out.println("Call saveInternals...........................");
  }
}