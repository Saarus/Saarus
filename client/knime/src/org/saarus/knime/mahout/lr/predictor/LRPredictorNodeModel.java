package org.saarus.knime.mahout.lr.predictor;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.DoubleCell;
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
/**
 * @author Tuan Nguyen
 */
public class LRPredictorNodeModel extends NodeModel {
  private static final NodeLogger logger = NodeLogger.getLogger(LRPredictorNodeModel.class);

  private LRPredictorSettings currentSettings = new LRPredictorSettings();
  
  protected LRPredictorNodeModel() {
    super(1, 1);
  }

  @Override
  protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec) throws Exception {
    System.out.println("call execute(update)..................");
    
    DataColumnSpec[] allColSpecs = new DataColumnSpec[2];
    allColSpecs[0] = new DataColumnSpecCreator("Map", DoubleCell.TYPE).createSpec();
    allColSpecs[1] = new DataColumnSpecCreator("Reduce", DoubleCell.TYPE).createSpec();
    DataTableSpec outputSpec = new DataTableSpec(allColSpecs);
    // the execution context will provide us with storage capacity, in this
    // case a data container to which we will add rows sequentially
    // Note, this container can also handle arbitrary big data tables, it
    // will buffer to disc if necessary.
    BufferedDataContainer container = exec.createDataContainer(outputSpec);
    // let's add m_count rows to it

    int count = 0 ;
    while(count < 100) {
      RowKey key = new RowKey("Row " + count);
      // the cells of the current row, the types of the cells must match
      // the column spec (see above)
      DataCell[] cells = new DataCell[2];
      cells[0] = new DoubleCell(Math.random() * 100); 
      cells[1] = new DoubleCell(Math.random() * 100); 
      DataRow row = new DefaultRow(key, cells);
      container.addRowToTable(row);

      // check if the execution monitor was canceled
      try {
        exec.checkCanceled();
      } catch(CanceledExecutionException cancelEx) {
        System.out.println("catch CanceledExecutionException...") ;
        throw cancelEx ;
      }
      exec.setProgress(count/100d, "progress = " + count/100d);
      count++ ;
    }
    // once we are done, we close the container and return its table
    container.close();
    BufferedDataTable out = container.getTable();
    return new BufferedDataTable[]{out};
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
    currentSettings.saveSettings(settings) ;
    System.out.println("Call saveSettingsTo...........................");
  }

  /** {@inheritDoc} */
  @Override
  protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
    this.currentSettings.merge(new LRPredictorSettings(settings)) ;
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