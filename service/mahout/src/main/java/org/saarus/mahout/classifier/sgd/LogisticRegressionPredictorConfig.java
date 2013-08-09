package org.saarus.mahout.classifier.sgd;

import java.util.ArrayList;
import java.util.List;

import org.saarus.service.task.TaskUnitConfig;

public class LogisticRegressionPredictorConfig extends TaskUnitConfig {
  private String input  ;
  private String output ;
  private String modelLocation ;
  private List<String> fieldNames ;
  private boolean clusterMode = true;
  
  public String getInput() {  return input; }
  public void setInput(String input) { this.input = input; }

  public String getOutput() { return output; }
  public void setOutput(String output) { this.output = output; }

  public String getModelLocation() { return modelLocation; }
  public void setModelLocation(String loc) { this.modelLocation = loc; }
  
  public List<String> getFieldNames() { return fieldNames; }
  public void setFieldNames(List<String> fieldNames) { this.fieldNames = fieldNames; }
  
  public LogisticRegressionPredictorConfig addFieldName(String name) {
    if(fieldNames == null) fieldNames = new ArrayList<String>() ;
    fieldNames.add(name) ;
    return this ;
  }
  
  String[] getFieldNameArray() {
    return fieldNames.toArray(new String[fieldNames.size()]) ;
  }
  
  public boolean isClusterMode() { return clusterMode; }
  public void setClusterMode(boolean clusterMode) { this.clusterMode = clusterMode; }
}
