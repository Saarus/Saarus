package org.saarus.service.task;

import java.util.ArrayList;
import java.util.List;


public class Task {
  private String         owner; 
  private String         id            ;
  private String         description   ;
  private String[]       dependedOn    ;
  private String         taskHandler ;
  private long           taskSubmitWait = 0l ;
  private List<TaskUnit> taskUnits ;
  
  public Task() {} ;
  
  public Task(String handler, TaskUnit ... unit) {
    this.taskHandler = handler; 
    taskUnits = new ArrayList<TaskUnit>() ;
    for(TaskUnit sel : unit) taskUnits.add(sel) ;
  } ;
  
  public String getOwner() { return this.owner; }
  public void   setOwner(String owner) { this.owner = owner ; }
  
  public String getId() { return id; }
  public void   setId(String id) { this.id = id; }
  
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
  
  public String[] getDependedOn() { return dependedOn; }
  public void setDependedOn(String[] dependedOn) { this.dependedOn = dependedOn; }
  
  public String getTaskHandler() { return taskHandler; }
  public void setTaskHandler(String taskHandler) { this.taskHandler = taskHandler; }
  
  public long getTaskSubmitWait() { return this.taskSubmitWait ; }
  public void setTaskSubmitWait(long time) { taskSubmitWait = time ; }
  
  public List<TaskUnit> getTaskUnits() { return taskUnits; }
  public void setTaskUnits(List<TaskUnit> units) { this.taskUnits = units; }

  public void add(TaskUnit taskUnit) {
    if(taskUnits == null) taskUnits = new ArrayList<TaskUnit>() ;
    taskUnits.add(taskUnit) ;
  }
}