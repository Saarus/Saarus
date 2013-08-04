package org.saarus.service.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class TaskUnitResult<T> {
  private String        taskId = "" ;
  private long          startTime = -1l;
  private long          finishTime = -1l ;
  private float         progress ;
  private List<TaskLog> logs = new ArrayList<TaskLog>() ;
  private T             result ;

  public String getTaskId() { return taskId; }
  public void setTaskId(String taskId) { this.taskId = taskId; }

  public long getStartTime() { return startTime; }
  public void setStartTime(long startTime) { this.startTime = startTime; }

  public long getFinishTime() { return finishTime; }
  public void setFinishTime(long finishTime) { this.finishTime = finishTime; }

  public float getProgress() { return progress; }
  public void setProgress(float progress) { this.progress = progress;}

  public List<TaskLog> getLogs() { return logs;}
  public void setLogs(List<TaskLog> logs) { this.logs = logs; }

  public void add(TaskLog.LogLevel level, String log) {
    logs.add(new TaskLog(level, log)) ;
  }

  @JsonDeserialize(using = TaskUnitResultDeserializer.class)
  @JsonSerialize(using   = TaskUnitResultSerializer.class)
  public T getResult() { return result; }
  public void setResult(T result) { this.result = result; }

  public String toString() {
    StringBuilder b = new StringBuilder() ;
    String endTime = "" ;
    if(this.finishTime > 0) {
      endTime = new Date(this.finishTime).toString() ;
    }
    b.append("TaskUnit Result:\n") ;
    b.append("  taskUnit id     = ").append(taskId).append("\n") ;
    b.append("  start time  = ").append(new Date(startTime)).append("\n") ;
    b.append("  finish time = ").append(endTime).append("\n") ;
    b.append("  progress    = ").append(progress * 100).append("%\n") ;
    b.append("  Result      = ").append(result).append("\n") ;
    if(logs.size() > 0) {
      b.append("TaskUnit Log:\n") ;
      for(int i = 0; i < logs.size(); i++) {
        TaskLog log = logs.get(i) ;
        b.append("  ").append(log.toString()) ;
      }
    }
    return b.toString() ;
  }
  
  
}
