package org.saarus.service.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskManager {
  private Map<String, TaskUnitHandler> handlers = new HashMap<String, TaskUnitHandler>() ;
  private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5) ;
  private Map<String, FutureTaskHolder> runningTasks = new HashMap<String, FutureTaskHolder>() ;
  
  public void add(TaskUnitHandler handler) {
    handlers.put(handler.getName(), handler) ;
  }
  
  public List<TaskUnitHandler> getHandlers() {
    return new ArrayList<TaskUnitHandler>(handlers.values()) ;
  }
  
  public void setHandlers(List<TaskUnitHandler> handlers) {
    for(TaskUnitHandler handler : handlers) {
      this.handlers.put(handler.getName(), handler) ;
    }
  }
  
  public TaskResult execute(Task task)  {
    if(task.getOwner() == null) {
      task.setOwner("anonymous") ;
    }
    if(task.getId() == null) {
      task.setId(UUID.randomUUID().toString()) ;
    }
    CallableTask callableTask = new CallableTask(handlers, task) ;
    Future<TaskResult> futureTask = executor.submit(callableTask) ;
    FutureTaskHolder holder = new FutureTaskHolder(futureTask, callableTask.getTaskResult()) ;
    runningTasks.put(holder.getId(), holder) ;
    try {
      long waitTime = task.getTaskSubmitWait() ;
      if(waitTime < 0) {
        return callableTask.getTaskResult() ;
      } else if(waitTime == 0) {
        TaskResult tresult = futureTask.get() ;
        runningTasks.remove(holder.getId()) ;
        return tresult ;
      } else {
        TaskResult tresult = futureTask.get(waitTime, TimeUnit.MILLISECONDS) ;
        if(tresult.isFinished()) {
          runningTasks.remove(holder.getId()) ;
        }
        return tresult ;
      }
    } catch (Exception e) {
      TaskResult tresult = callableTask.getTaskResult() ;
      return tresult ;
    }
  }
  
  public TaskResult poll(String owner, String taskId) {
    String id = owner + "@" + taskId ;
    FutureTaskHolder holder  = runningTasks.get(id) ;
    if(holder == null) return null ;
    if(holder.futureTask.isDone() || holder.futureTask.isCancelled()) {
      runningTasks.remove(id) ;
    }
    return holder.getTaskResult() ;
  }
  
  static class FutureTaskHolder {
    Future<TaskResult> futureTask ;
    TaskResult taskResult ;
  
    public FutureTaskHolder(Future<TaskResult> futureTask, TaskResult taskResult) {
      this.futureTask = futureTask ;
      this.taskResult = taskResult ;
    }
  
    public String getId() { return getOwner() + "@" + getTaskId() ; }
    
    public String getTaskId() { return taskResult.getTask().getId() ; }
    
    public String getOwner() { return taskResult.getTask().getOwner() ; }
    
    public TaskResult getTaskResult() { return taskResult ; }
    
    public Future<TaskResult> getFuture() { return this.futureTask ; }
  }
}