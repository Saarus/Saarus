package org.saarus.knime.mahout.lr.predictor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.saarus.mahout.classifier.sgd.LogisticRegressionPredictorConfig;
import org.saarus.mahout.classifier.sgd.LogisticRegressionTaskHandler;
import org.saarus.service.task.Task;
import org.saarus.service.task.TaskUnit;
import org.saarus.util.json.JSONSerializer;

public class LRPredictorConfig {
  private String name ;
  private String description ;
  LogisticRegressionPredictorConfig predictConfig = new LogisticRegressionPredictorConfig();
  
  public LRPredictorConfig() {}
  
  public LRPredictorConfig(NodeSettingsRO settings) throws InvalidSettingsException {
    if(!settings.containsKey("predictConfigJson")) return ;
    name = settings.getString("name");
    description = settings.getString("description");
    String json = settings.getString("predictConfigJson") ;
    try {
      predictConfig = JSONSerializer.JSON_SERIALIZER.fromString(json, LogisticRegressionPredictorConfig.class) ;
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e) ;
    }
  }
  
  
  
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public void saveSettings(NodeSettingsWO settings) {
    if(predictConfig == null) return ;
    settings.addString("name", name) ;
    settings.addString("description", description) ;
    try {
      String json = JSONSerializer.JSON_SERIALIZER.toString(predictConfig);
      settings.addString("predictConfigJson", json)  ;
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(e) ;
    }
  }
  
  public Task getGeneratedTask() throws IOException {
    Task task = new Task() ;
    task.setTaskHandler(LogisticRegressionTaskHandler.NAME) ;
    task.setTaskSubmitWait(3000l) ;
    task.setDescription("Run Logistic Regression Training") ;
    List<TaskUnit> units = new ArrayList<TaskUnit>() ;
    for(TaskUnit sel : getGeneratedTaskUnits(predictConfig)) {
      units.add(sel) ;
    }
    task.setTaskUnits(units) ;
    return task ;
  }

  public TaskUnit[] getGeneratedTaskUnits(LogisticRegressionPredictorConfig config) {
    TaskUnit taskUnit = new TaskUnit() ;
    taskUnit.setId(getTaskUnitId(config)) ;
    taskUnit.setName("predict") ;
    taskUnit.setTaskUnitConfig(predictConfig) ;
    return new TaskUnit[] { taskUnit } ;
  }
  
  public String getTaskUnitId(LogisticRegressionPredictorConfig config) { 
    return "predict:" + config.getInput() ; 
  }
  
  public String toString() {
    if(predictConfig != null) return predictConfig.toString() ;
    return ""  ;
  }
}