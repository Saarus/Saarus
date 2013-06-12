package org.saarus.service.task;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class CallableTask implements Callable<TaskResult> {
  private Map<String, TaskUnitHandler> handlers ;
  private Task task ;
  private TaskResult tresult  ;
  
  public CallableTask(Map<String, TaskUnitHandler> handlers, Task task) {
    this.handlers = handlers ;
    this.task = task ;
    this.tresult = new TaskResult() ;
    this.tresult.setTask(task) ;
  }
  
  public TaskResult getTaskResult() { return this.tresult ; }
  
  public TaskResult call()  {
    tresult.setStartTime(System.currentTimeMillis()) ;
    TaskUnitHandler handler = handlers.get(task.getTaskHandler()) ;
    List<TaskUnit> units = task.getTaskUnits() ;
    final String errMesg = "Service %1s cannot handle the taskUnit unit %2s" ;
    for(int i = 0; i < units.size(); i++) {
      TaskUnit unit = units.get(i) ;
      if(unit.getId() == null) {
        unit.setId(task.getId() + ":TaskUnit-" + i) ;
      }
      CallableTaskUnit<?> callableTaskUnit = null ;
      if(handler == null) {
        callableTaskUnit =
            CallableTaskUnit.createCallableTaskUnit(unit, String.format(errMesg, task.getTaskHandler(), unit.getName()));
      } else {
        callableTaskUnit = handler.getCallableTaskUnit(unit) ;
        if(callableTaskUnit == null) {
          callableTaskUnit =
              CallableTaskUnit.createCallableTaskUnit(unit, String.format(errMesg, task.getTaskHandler(), unit.getName()));
        }
      }
      TaskUnitResult<?> unitResult = callableTaskUnit.call() ;
      tresult.add(unitResult) ;
    }
    tresult.setFinishTime(System.currentTimeMillis()) ;
    return tresult ;
  }
}
