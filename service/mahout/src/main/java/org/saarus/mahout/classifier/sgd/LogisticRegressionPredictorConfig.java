package org.saarus.mahout.classifier.sgd;

import org.saarus.service.task.TaskUnitConfig;

public class LogisticRegressionPredictorConfig extends TaskUnitConfig {
  private String input  ;
  private String output ;
  private String modelLocation ;
  boolean auc ; 
  boolean confusion ;
  private boolean clusterMode = true;
  
  public String getInput() {  return input; }
  public void setInput(String input) { this.input = input; }

  public String getOutput() { return output; }
  public void setOutput(String output) { this.output = output; }

  public String getModelLocation() { return modelLocation; }
  public void setModelLocation(String loc) { this.modelLocation = loc; }
  
  public boolean isAuc() { return auc; }
  public void setAuc(boolean auc) { this.auc = auc; }
  
  public boolean isConfusion() { return confusion; }
  public void setConfusion(boolean confusion) { this.confusion = confusion;}
  
  public boolean isClusterMode() { return clusterMode; }
  public void setClusterMode(boolean clusterMode) { this.clusterMode = clusterMode; }
}
