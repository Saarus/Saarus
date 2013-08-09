package org.saarus.knime.mahout.lr.learner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.config.ConfigRO;
import org.saarus.mahout.classifier.sgd.LogisticRegressionTaskHandler;
import org.saarus.mahout.classifier.sgd.LogisticRegressionTrainerConfig;
import org.saarus.service.task.Task;
import org.saarus.service.task.TaskUnit;
import org.saarus.util.json.JSONSerializer;

public class LRLearnerConfig {
  final static public String MAHOUT_CONFIG = "mahout.config" ;
  
  String name = "" ;
  String description = "";
  LogisticRegressionTrainerConfig trainConfig = new LogisticRegressionTrainerConfig();
  
  public LRLearnerConfig() {}
  
  public LRLearnerConfig(NodeSettingsRO settings) throws InvalidSettingsException {
    if(!settings.containsKey(MAHOUT_CONFIG)) return ;
    ConfigRO config = settings.getConfig(MAHOUT_CONFIG) ;
    name = config.getString("name");
    description = config.getString("description");
    String trainConfigJson = config.getString("trainConfigJson");
    try {
      trainConfig = JSONSerializer.JSON_SERIALIZER.fromString(trainConfigJson, LogisticRegressionTrainerConfig.class) ;
    } catch (IOException e) {
      throw new RuntimeException(e) ;
    }
  }
  
  public String getName() { return name ; }
  public void setName(String name) { this.name = name; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public void saveSettings(NodeSettingsWO settings) {
    if(trainConfig == null) return ;
    settings.addString("name", name) ;
    settings.addString("description", description) ;
    try {
      String trainConfigJson = JSONSerializer.JSON_SERIALIZER.toString(trainConfig) ;
      settings.addString("trainConfigJson", trainConfigJson) ;
    } catch (IOException e) {
      throw new RuntimeException(e) ;
    }
  }
  
  public void merge(LRLearnerConfig other) {
    trainConfig = other.trainConfig ;
  }
  
  public Task getGeneratedTask() throws IOException {
    Task task = new Task() ;
    task.setTaskHandler(LogisticRegressionTaskHandler.NAME) ;
    task.setTaskSubmitWait(3000l) ;
    task.setDescription("Run Logistic Regression Training") ;
    List<TaskUnit> units = new ArrayList<TaskUnit>() ;
    for(TaskUnit sel : getGeneratedTaskUnits(trainConfig)) {
      units.add(sel) ;
    }
    task.setTaskUnits(units) ;
    return task ;
  }

  public TaskUnit[] getGeneratedTaskUnits(LogisticRegressionTrainerConfig config) {
    TaskUnit taskUnit = new TaskUnit() ;
    taskUnit.setId(getTaskUnitId(config)) ;
    taskUnit.setName("train") ;
    taskUnit.setTaskUnitConfig(config) ;
    return new TaskUnit[] { taskUnit } ;
  }

  
  public String toString() {
    if(trainConfig != null) return trainConfig.toString() ;
    return ""  ;
  }

  static public String getTaskUnitId(LogisticRegressionTrainerConfig config) {
    return config.getInput() + "_train";
  }
}
