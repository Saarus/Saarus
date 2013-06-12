package org.saarus.service.task;

public interface TaskUnitHandler {
  public String getName() ;
  
  public <T> CallableTaskUnit<T> getCallableTaskUnit(TaskUnit taskUnit) ;
}