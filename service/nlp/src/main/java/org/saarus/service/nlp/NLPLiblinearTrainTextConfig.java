package org.saarus.service.nlp;

import org.saarus.service.task.TaskUnitConfig;

public class NLPLiblinearTrainTextConfig implements TaskUnitConfig {
  private String table ;
  private String textField;
  private String labelField ;
  private String modelOutputLoc ;
  private String tmpDir = "target/nlpTmp";
  
  public String getTable() { return table; }
  public void setTable(String table) { this.table = table; }
  
  public String getTextField() { return textField; }
  public void setTextField(String textField) { this.textField = textField; }
  
  public String getLabelField() { return labelField; }
  public void setLabelField(String labelField) { this.labelField = labelField; }
  
  public String getModelOutputLoc() { return modelOutputLoc; }
  public void setModelOutputLoc(String modelOutputLoc) { this.modelOutputLoc = modelOutputLoc; }
  
  public String getTmpDir() { return tmpDir; }
  public void setTmpDir(String tmpDir) { this.tmpDir = tmpDir; }
  
}
