package org.saarus.service.task;

import java.util.concurrent.Callable;

abstract public class CallableTaskUnit<T> implements Callable<TaskUnitResult<T>>{
  protected TaskUnit taskUnit ;
  protected TaskUnitResult<T> taskUnitResult ;
  
  public CallableTaskUnit(TaskUnit task, TaskUnitResult<T> result) {
    this.taskUnit = task ;
    this.taskUnitResult = result ;
    result.setTaskId(task.getId()) ;
  }
  
  public TaskUnitResult<T> getTaskUnitResult() { return this.taskUnitResult ; }
  
  abstract public T doCall() throws Exception ;
  
  public TaskUnitResult<T> call() {
    taskUnitResult.setStartTime(System.currentTimeMillis()) ;
    try {
      T result = doCall() ;
      taskUnitResult.setResult(result) ;
    } catch(Throwable e) {
      System.out.println("Task: " + taskUnit.getTaskLine());
      e.printStackTrace();
      taskUnitResult.add(TaskLog.LogLevel.ERROR, e.getMessage()) ;
    } finally {
      taskUnitResult.setFinishTime(System.currentTimeMillis()) ;
    }
    return taskUnitResult ;
  }
  
  public TaskUnit getTask() { return taskUnit; }
  public void setTask(TaskUnit task) { this.taskUnit = task; }
  
  static public <T> CallableTaskUnit<T> createCallableTaskUnit(TaskUnit tunit, final T retVal) {
    CallableTaskUnit<T> callableUnit = new CallableTaskUnit<T>(tunit, new TaskUnitResult<T>()) {
      public T doCall() throws Exception {
        return retVal ;
      }
    };
    return callableUnit ;
  }
}