package org.saarus.knime.mahout.lr.predictor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.config.ConfigRO;
import org.knime.core.node.config.ConfigWO;
import org.saarus.mahout.classifier.sgd.LogisticRegressionTaskHandler;
import org.saarus.service.task.Parameters;
import org.saarus.service.task.Task;
import org.saarus.service.task.TaskUnit;

public class LRPredictorConfigs {
  final static public String MAHOUT_CONFIG = "mahout.config" ;
  
  MahoutConfig mahoutConfig  ;
  
  public LRPredictorConfigs() {}
  
  public LRPredictorConfigs(NodeSettingsRO settings) throws InvalidSettingsException {
    if(!settings.containsKey(MAHOUT_CONFIG)) return ;
    ConfigRO config = settings.getConfig(MAHOUT_CONFIG) ;
    mahoutConfig = new MahoutConfig() ;
    mahoutConfig.name = config.getString("name");
    mahoutConfig.description = config.getString("description");
    
    mahoutConfig.input = config.getString("input"); 
    mahoutConfig.output = config.getString("output");
    mahoutConfig.model = config.getString("model");
    mahoutConfig.colHeaders = config.getString("colHeaders");  
    mahoutConfig.auc = config.getBoolean("auc");
    mahoutConfig.confusion = config.getBoolean("confusion");
    mahoutConfig.clusterMode = config.getBoolean("clusterMode");
  }
  
  public void saveSettings(NodeSettingsWO settings) {
    if(mahoutConfig == null) return ;
    ConfigWO config = settings.addConfig(MAHOUT_CONFIG) ;
    config.addString("name", mahoutConfig.name) ;
    config.addString("description", mahoutConfig.description) ;
    config.addString("input", mahoutConfig.input)  ;  
    config.addString("output", mahoutConfig.output) ;
    config.addString("model", mahoutConfig.model) ;
    config.addString("colHeaders", mahoutConfig.colHeaders)  ;
    config.addBoolean("auc", mahoutConfig.auc)  ;
    config.addBoolean("confusion", mahoutConfig.confusion)  ;
    config.addBoolean("clusterMode", mahoutConfig.clusterMode)  ;
  }
  
  public void merge(LRPredictorConfigs other) {
    mahoutConfig = other.mahoutConfig ;
  }
  
  public Task getGeneratedTask() throws IOException {
    Task task = new Task() ;
    task.setTaskHandler(LogisticRegressionTaskHandler.NAME) ;
    task.setTaskSubmitWait(3000l) ;
    task.setDescription("Run Logistic Regression Training") ;
    List<TaskUnit> units = new ArrayList<TaskUnit>() ;
    for(TaskUnit sel : mahoutConfig.getGeneratedTaskUnits()) {
      units.add(sel) ;
    }
    task.setTaskUnits(units) ;
    return task ;
  }
  
  public String toString() {
    if(mahoutConfig != null) return mahoutConfig.toString() ;
    return ""  ;
  }
  
  static public class MahoutConfig {
    String name ;
    String description ;
    String input ;
    String output ;
    String model ;
    String colHeaders ;
    boolean auc ; 
    boolean confusion ; 
    boolean clusterMode ;
    
    public TaskUnit[] getGeneratedTaskUnits() {
      TaskUnit taskUnit = new TaskUnit() ;
      taskUnit.setId(getTaskUnitId()) ;
      taskUnit.setName("predict") ;
      Parameters params = taskUnit.getParameters() ;
      params.setString("input", input) ;
      params.setString("output", output) ;
      params.setString("model", model) ;
      params.setString("col-header", colHeaders) ;
      params.setString("auc", Boolean.toString(auc)) ;
      params.setString("confusion", Boolean.toString(confusion)) ;
      params.setString("clusterMode", Boolean.toString(clusterMode)) ;
      return new TaskUnit[] { taskUnit } ;
    }

    public String getTaskUnitId() {
      return name + "_predict";
    }
    
    public String toString() {
      StringBuilder b = new StringBuilder() ;
      b.append("Config: ").append("Name = ").append(name).append(", Desc = ").append(description) ;
      return b.toString() ;
    }
  }
}
