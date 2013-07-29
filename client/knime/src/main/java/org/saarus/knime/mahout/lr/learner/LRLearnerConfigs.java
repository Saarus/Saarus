package org.saarus.knime.mahout.lr.learner;

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

public class LRLearnerConfigs {
  final static public String MAHOUT_CONFIG = "mahout.config" ;
  
  MahoutConfig mahoutConfig  ;
  
  public LRLearnerConfigs() {}
  
  public LRLearnerConfigs(NodeSettingsRO settings) throws InvalidSettingsException {
    if(!settings.containsKey(MAHOUT_CONFIG)) return ;
    ConfigRO config = settings.getConfig(MAHOUT_CONFIG) ;
    mahoutConfig = new MahoutConfig() ;
    mahoutConfig.name = config.getString("name");
    mahoutConfig.description = config.getString("description");
    
    mahoutConfig.passes = config.getString("passes"); //  --passes 100,
    mahoutConfig.rate  = config.getString("rate");  // --rate 50
    mahoutConfig.lambda = config.getString("lambda");  //--lambda 0.001
    mahoutConfig.features = config.getString("features"); // --features 21
    mahoutConfig.categories = config.getString("categories"); //  --categories 2
    //--predictors  x, y, xx, xy, yy, a, b, c
    mahoutConfig.predictors  = config.getString("predictors") ;  
    mahoutConfig.input = config.getString("input"); //--input donut.csv 
    mahoutConfig.target = config.getString("target"); //--target color
    mahoutConfig.output = config.getString("output");// --output donut.model
  }
  
  public void saveSettings(NodeSettingsWO settings) {
    if(mahoutConfig == null) return ;
    ConfigWO config = settings.addConfig(MAHOUT_CONFIG) ;
    config.addString("name", mahoutConfig.name) ;
    config.addString("description", mahoutConfig.description) ;
    config.addString("passes", mahoutConfig.passes) ; //  --passes 100,
    config.addString("rate", mahoutConfig.rate)  ;  // --rate 50
    config.addString("lambda", mahoutConfig.lambda);  //--lambda 0.001
    config.addString("features", mahoutConfig.features) ; // --features 21
    config.addString("categories", mahoutConfig.categories) ; //  --categories 2
    //--predictors  x, y, xx, xy, yy, a, b, c
    config.addString("predictors", mahoutConfig.predictors)  ;  
    config.addString("input", mahoutConfig.input)  ; //--input donut.csv 
    config.addString("target", mahoutConfig.target)  ; //--target color
    config.addString("output", mahoutConfig.output)  ;// --output donut.model
  }
  
  public void merge(LRLearnerConfigs other) {
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
    
    String passes = "100"; //  --passes 100,
    String rate  = "50";  // --rate 50
    String lambda = "0.001";  //--lambda 0.001
    String features = "20"; // --features 21
    String categories = "2"; //  --categories 2
    //--predictors  x, y, xx, xy, yy, a, b, c
    String predictors  = "n:x | n:y | n:xx | n:xy | n:yy | n:a | n:b | n:c";  
    String input = "donut-train.csv" ; //--input donut.csv 
    String target = "color" ; //--target color
    String output = "donut.model" ;// --output donut.model
    
    public TaskUnit[] getGeneratedTaskUnits() {
      TaskUnit taskUnit = new TaskUnit() ;
      taskUnit.setId(getTaskUnitId()) ;
      taskUnit.setName("train") ;
      Parameters params = taskUnit.getParameters() ;
      params.setString("input", input) ;
      params.setString("output", output) ;
      params.setString("target", target) ;
      params.setString("categories", categories) ;
      params.setString("predictors", predictors) ;
      params.setString("features", features) ;
      params.setString("passes", passes) ;
      params.setString("rate", rate) ;
      params.setString("lambda", lambda) ;
      return new TaskUnit[] { taskUnit } ;
    }

    public String getTaskUnitId() {
      return name + "_train";
    }
    
    public String toString() {
      StringBuilder b = new StringBuilder() ;
      b.append("Config: ").append("Name = ").append(name).append(", Desc = ").append(description) ;
      return b.toString() ;
    }
  }
}
