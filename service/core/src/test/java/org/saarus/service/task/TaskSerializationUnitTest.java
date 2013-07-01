package org.saarus.service.task;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;
import org.saarus.service.task.Task;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.util.JSONSerializer;

public class TaskSerializationUnitTest {
  @Test
  public void testDeSer() throws IOException {
    TaskUnit taskUnit = createTaskUnit("execute", "Select count(*) from user") ;
    
    String taskUnitJson = JSONSerializer.JSON_SERIALIZER.toString(taskUnit) ;
    System.out.println(taskUnitJson);
    
    taskUnit = JSONSerializer.JSON_SERIALIZER.fromString(taskUnitJson, TaskUnit.class) ;
    Assert.assertEquals("execute", taskUnit.getName()) ;
    Assert.assertEquals(3, taskUnit.getParameters().size()) ;
  
    Task task = new Task() ;
    task.setId("taskUnit") ;
    task.setDescription("execute some hive sql queries....") ;
    task.setTaskHandler("HiveService") ;
    task.add(taskUnit) ;
    String taskJson = JSONSerializer.JSON_SERIALIZER.toString(task) ;
    System.out.println(taskJson);
    
    System.out.println("--------------------------------------------------");
    
    TaskResult taskResult = new TaskResult() ;
    taskResult.setTask(task) ;
    TaskUnitResult<String[]> taskUnitResult = new TaskUnitResult<String[]>() ;
    taskUnitResult.setTaskId(taskUnit.getId()) ;
    taskUnitResult.setResult(new String[] {"test1", "test2" }) ;
    taskResult.add(taskUnitResult) ;
    
    String taskResultJson = JSONSerializer.JSON_SERIALIZER.toString(taskResult) ;
    System.out.println(taskResultJson);
    taskResult = JSONSerializer.JSON_SERIALIZER.fromString(taskResultJson, TaskResult.class) ;
    taskUnitResult = (TaskUnitResult<String[]>) taskResult.getTaskUnitResult(taskUnitResult.getTaskId()) ;
    Assert.assertTrue(taskUnitResult.getResult() instanceof String[]) ;
  }
  
  private TaskUnit createTaskUnit(String name, String task) {
    TaskUnit tunit = new TaskUnit(task) ;
    tunit.setId(name) ;
    tunit.setName(name) ;
    tunit.getParameters().setInteger("intParam", 100) ;
    tunit.getParameters().setString("stringParam", "a string") ;
    tunit.getParameters().setObject("stringArray", new Object[] {"test1", 2 }) ;
    return tunit ;
  }
}
