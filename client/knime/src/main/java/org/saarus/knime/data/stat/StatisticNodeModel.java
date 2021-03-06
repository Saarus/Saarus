package org.saarus.knime.data.stat;

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
import org.saarus.client.RESTClient;
import org.saarus.knime.ServiceContext;
import org.saarus.knime.data.stat.StatisticConfigs.StatisticConfig;
import org.saarus.service.sql.QueryResult;
import org.saarus.service.task.Task;
import org.saarus.service.task.TaskResult;
import org.saarus.service.task.TaskUnitResult;
/**
 * @author Tuan Nguyen
 */
public class StatisticNodeModel extends NodeModel {
  private static final NodeLogger logger = NodeLogger.getLogger(StatisticNodeModel.class);

  private StatisticConfigs currentConfigs = new StatisticConfigs();
  
  protected StatisticNodeModel() {
    super(1, 1);
  }

  @Override
  protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec) throws Exception {
    try {
      System.out.println("call execute(update)..................");
      ClientContext context = ServiceContext.getInstance().getClientContext() ;
      RESTClient restClient = context.getBean(RESTClient.class) ;
      Task task = currentConfigs.getGeneratedTask() ;
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
      
      DataColumnSpec[] allColSpecs = new DataColumnSpec[2];
      allColSpecs[0] = new DataColumnSpecCreator("Table", StringCell.TYPE).createSpec();
      allColSpecs[1] = new DataColumnSpecCreator("Data", StringCell.TYPE).createSpec();
      DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
      BufferedDataContainer container = exec.createDataContainer(outputSpec);

      Iterator<StatisticConfig> i = currentConfigs.getConfigs().iterator(); 
      int count = 0 ;
      while(i.hasNext()) {
        StatisticConfig config = i.next() ;
        TaskUnitResult<QueryResult> unitResult = 
          (TaskUnitResult<QueryResult>)taskResult.getTaskUnitResult(config.getTaskUnitId()) ;
        QueryResult qresult = unitResult.getResult() ;
        String[] column = qresult.getColumn() ;
        Object[][] data = qresult.getData() ;
        StringBuilder b = new StringBuilder() ;
        for(int row = 0; row < data.length; row++) {
          b.append("{") ;
          for(int col = 0; col < column.length; col++) {
            if(col > 0) b.append(", ");
            b.append("\"").append(column[col]).append("\"").append(": ");
            b.append(data[row][col]) ;
          }
          b.append("}") ;
        }
        DataCell[] cells = { 
            new StringCell(config.table),
            new StringCell(b.toString()),
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

  @Override
  protected void reset() {
    System.out.println("Call reset..........................");
  }

  @Override
  protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
    System.out.println("Call configure(Update)");
    return new DataTableSpec[]{null};
  }

  @Override
  protected void saveSettingsTo(final NodeSettingsWO settings) {
    currentConfigs.saveSettings(settings) ;
    System.out.println("Call saveSettingsTo...........................");
  }

  @Override
  protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
    //currentConfigs.merge(new StatisticConfigs(settings)) ;
    currentConfigs = new StatisticConfigs(settings) ;
    System.out.println(currentConfigs) ;
  }


  @Override
  protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
    System.out.println("Call validateSettings...........................");
  }

  @Override
  protected void loadInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException {
    System.out.println("Call loadInternals...........................");
  }

  @Override
  protected void saveInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException {
    System.out.println("Call saveInternals...........................");
  }
}