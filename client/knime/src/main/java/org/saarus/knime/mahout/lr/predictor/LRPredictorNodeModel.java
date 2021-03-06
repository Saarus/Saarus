package org.saarus.knime.mahout.lr.predictor;

import java.io.File;
import java.io.IOException;

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
import org.saarus.mahout.classifier.sgd.LogisticRegressionPredictorConfig;
import org.saarus.service.task.Task;
import org.saarus.service.task.TaskResult;
import org.saarus.service.task.TaskUnitResult;
/**
 * @author Tuan Nguyen
 */
public class LRPredictorNodeModel extends NodeModel {
  private static final NodeLogger logger = NodeLogger.getLogger(LRPredictorNodeModel.class);

  private LRPredictorConfig config = new LRPredictorConfig();
  
  protected LRPredictorNodeModel() {
    super(1, 1);
  }

  @Override
  protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec) throws Exception {
    try {
      System.out.println("call execute(update)..................");
      ClientContext context = ServiceContext.getInstance().getClientContext() ;
      RESTClient restClient = context.getBean(RESTClient.class) ;
      Task task = config.getGeneratedTask() ;
      TaskResult taskResult = restClient.submitTask(task) ;
      int count = 0 ;
      while(!taskResult.isFinished()) {
        Thread.sleep(1000) ;
        try {
          exec.checkCanceled();
          taskResult = restClient.pollTask(taskResult.getTask()) ;
          exec.setProgress(taskResult.getProgress(), "progress = " + (taskResult.getProgress() * 100) + "%, check " + count);
        } catch(CanceledExecutionException cancelEx) {
          System.out.println("catch CanceledExecutionException...") ;
          throw cancelEx ;
        } catch(Throwable t) {
          t.printStackTrace() ;
          throw new CanceledExecutionException(t.getMessage()) ;
        }
        count++ ;
      }
      
      DataColumnSpec[] allColSpecs = new DataColumnSpec[1];
      allColSpecs[0] = new DataColumnSpecCreator("Data", StringCell.TYPE).createSpec();
      DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
      BufferedDataContainer container = exec.createDataContainer(outputSpec);
      
      LogisticRegressionPredictorConfig predictorConfig = this.config.predictConfig ;
      TaskUnitResult<Boolean> unitResult = 
          (TaskUnitResult<Boolean>)taskResult.getTaskUnitResult(config.getTaskUnitId(predictorConfig)) ;
      System.out.println(unitResult) ;
      container.addRowToTable(new DefaultRow(new RowKey("Output"), new StringCell(predictorConfig.getOutput())));
      container.addRowToTable(new DefaultRow(new RowKey("Success"), new StringCell(unitResult.getResult().toString())));

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
    config.saveSettings(settings) ;
    System.out.println("Call saveSettingsTo...........................");
  }

  /** {@inheritDoc} */
  @Override
  protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
    config = new LRPredictorConfig(settings) ;
    System.out.println("Load loadValidatedSettings(merge)") ;
    System.out.println(this.config) ;
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