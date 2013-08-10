package org.saarus.mahout.classifier.sgd;

import java.util.ArrayList;
import java.util.List;

import org.saarus.service.task.TaskUnitConfig;

public class LogisticRegressionTrainerConfig extends TaskUnitConfig {
  private String input  ;
  private String modelOutputLocation ;
  private String target ;
  private String  categories =  "2" ;

  private List<Predictor> predictors  = new ArrayList<Predictor>() ;
  private String features = "1000" ;
  private String passes = "100"  ;
  private String rate = "50" ;
  private String lambda = "0.001" ;
  private int maxRead = -1;

  public String getInput() {  return input; }
  public void setInput(String input) { this.input = input; }

  public String getModelOutputLocation() { return modelOutputLocation; }
  public void   setModelOutputLocation(String output) { this.modelOutputLocation = output; }

  public String getTarget() { return target; }
  public void setTarget(String target) { this.target = target; }

  public String getCategories() { return categories; }
  public void setCategories(String categories) { this.categories = categories; }

  public LogisticRegressionTrainerConfig addPredictor(String fieldName, String type) {
    predictors.add(new Predictor(fieldName, type)) ;
    return this ;
  }
  
  public List<Predictor> getPredictors() { return predictors; }
  public void setPredictors(List<Predictor> predictors) { this.predictors = predictors; }

  String getPredictorParameters() {
    StringBuilder b = new StringBuilder() ;
    if(predictors != null) {
      for(int i = 0; i < predictors.size(); i++) {
        Predictor p = predictors.get(i) ;
        if(i > 0) b.append(" | ") ;
        b.append(p.getType()).append(":").append(p.getName()) ;
      }
    }
    return b.toString() ;
  }
  
  public String getFeatures() { return features; }
  public void setFeatures(String features) { this.features = features; }

  public String  getPasses() { return passes; }
  public void setPasses(String passes) { this.passes = passes; }

  public String getRate() { return rate; }
  public void setRate(String rate) { this.rate = rate; }

  
  public String getLambda() { return lambda; }
  public void setLambda(String lambda) { this.lambda = lambda; }
  
  public int getMaxRead() { return this.maxRead ; }
  public void setMaxRead(int max) { this.maxRead = max ; }
  
  static public class Predictor {
    private String name ;
    private String type ;
    
    public Predictor() {} 
    
    public Predictor(String name, String type) {
      this.name = name ;
      this.type = type ;
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
  }
}
