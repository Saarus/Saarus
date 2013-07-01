package org.saarus.service.task;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.saarus.service.util.StringUtil;

public class TaskUnit {
  private String id ;
  private String name ;
  private String description ;
  private String[] task ;
  private Parameters parameters = new Parameters() ;
  
  public TaskUnit() { }
  
  public TaskUnit(String task) {
    this.task = new String[] {task} ;
  }
  
  public TaskUnit(String name, String task) {
    this.name = name ;
    if(task != null) {
      this.task = new String[] {task} ;
    }
  }
  
  public String getId() { return id ; }
  public void   setId(String id) { this.id = id ; }
  
  public String getName() { return this.name ; }
  public void   setName(String name) { this.name = name ; }
  
  public String getDescription()         { return this.description ; }
  public void   setDescription(String s) { this.description = s ; }
  
  public String[] getTask() { return task; }
  public void     setTask(String[] task) { this.task = task; }
  
  @JsonIgnore
  public String getTaskLine() {
    return StringUtil.joinStringArray(task, "\n") ;
  }
  @JsonIgnore
  public void     setTaskLine(String task) { 
    this.task = new String[] { task }; 
  }
  
  
  public Parameters getParameters() { return this.parameters ; }
  public void       setParameters(Parameters params) { this.parameters = params ; }
  
  public String toString() {
    StringBuilder b = new StringBuilder() ;
    b.append("TaskUnit Info: \n") ;
    b.append("  name         = ").append(name).append("\n") ;
    b.append("  taskUnit         = ").append(task).append("\n") ;
    return b.toString() ;
  }
}