package org.saarus.mahout.classifier.sgd;

public class PredictorType {
  private String type ;
  private String name;
  
  public PredictorType(String string) {
    int idx = string.indexOf(":") ;
    if(idx <= 0) {
      throw new RuntimeException("Unkown type for " + string + ", expect the predictor type in the format type:predictor-name") ;
    }
    this.type = string.substring(0, idx) ;
    this.name = string.substring(idx + 1) ;
  }
  
  public String getName() { return name ; }
  
  public String getType() { return this.type ; }
}
