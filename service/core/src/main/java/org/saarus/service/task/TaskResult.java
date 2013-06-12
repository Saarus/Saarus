package org.saarus.service.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

public class TaskResult {
  private Task  task ;
  private long  startTime = -1l;
  private long  finishTime = -1l ;
  private Map<String, TaskUnitResult<?>> taskResults = new LinkedHashMap<String,TaskUnitResult<?>>() ;
  
  public Task getTask() { return task; }
  public void setTask(Task task) { this.task = task; }
  
  public long getStartTime() { return startTime; }
  public void setStartTime(long startTime) { this.startTime = startTime; }
  
  public long getFinishTime() { return finishTime; }
  public void setFinishTime(long finishTime) { this.finishTime = finishTime; }
  
  @JsonIgnore
  public boolean isFinished() { return finishTime > 0 ; }
  
  @JsonIgnore
  public float getProgress() { 
    return taskResults.size()/(float)task.getTaskUnits().size(); 
  }
  
  
  public List<TaskUnitResult<?>> getTaskUnitResults() { 
    List<TaskUnitResult<?>> holder = new ArrayList<TaskUnitResult<?>>() ;
    holder.addAll(taskResults.values()) ;
    return holder; 
  }
  
  public void setTaskUnitResults(List<TaskUnitResult<?>> taskResults) { 
    for(TaskUnitResult<?> sel : taskResults) {
      this.taskResults.put(sel.getTaskId(), sel);
    }
  }
  
  public TaskUnitResult<?> getTaskUnitResult(String taskId) {
    return taskResults.get(taskId) ;
  }
  
  public void add(TaskUnitResult<?> tuResult) {
    taskResults.put(tuResult.getTaskId(), tuResult) ;
  }
  
  public String toString() {
    StringBuilder b = new StringBuilder() ;
    b.append(task.toString()).append("\n") ;
    Iterator<TaskUnitResult<?>> i = taskResults.values().iterator() ;
    while(i.hasNext()) {
      TaskUnitResult<?> unitResult = i.next() ;
      b.append(unitResult.toString()).append("\n") ;
    }
    return b.toString() ;
  }
  
}
