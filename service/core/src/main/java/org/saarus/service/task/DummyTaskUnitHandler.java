package org.saarus.service.task;


public class DummyTaskUnitHandler implements TaskUnitHandler {

  public String getName() { return "Dummy" ; }
  
  public CallableTaskUnit<?> getCallableTaskUnit(TaskUnit taskUnit) {
    String name = taskUnit.getName();
    if("hello".equals(name)) return hello(taskUnit);
    else if("ping".equals(name)) return ping(taskUnit);
    else if("wait".equals(name)) return wait(taskUnit) ;
    return null;
  }
  
  private CallableTaskUnit<String> hello(TaskUnit task) {
    CallableTaskUnit<String> executor = new CallableTaskUnit<String>(task, new TaskUnitResult<String>()) {
      public String doCall() throws Exception {
        return "hello from Dummy..." ;
      }
    };
    return executor ;
  }
  
  private CallableTaskUnit<Boolean> ping(TaskUnit task) {
    CallableTaskUnit<Boolean> executor = new CallableTaskUnit<Boolean>(task, new TaskUnitResult<Boolean>()) {
      public Boolean doCall() throws Exception {
        return true ;
      }
    };
    return executor ;
  }
  
  private CallableTaskUnit<Long> wait(TaskUnit task) {
    CallableTaskUnit<Long> executor = new CallableTaskUnit<Long>(task, new TaskUnitResult<Long>()) {
      public Long doCall() throws Exception {
        long start = System.currentTimeMillis() ;
        Thread.sleep(taskUnit.getParameters().getLong("wait", 1000l)) ;
        long waitTime = System.currentTimeMillis() - start ;
        return waitTime ;
      }
    };
    return executor ;
  }
}