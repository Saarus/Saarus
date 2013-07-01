package org.saarus.service.task;

import junit.framework.Assert;

import org.junit.Test;
import org.saarus.service.task.DummyTaskUnitHandler;
import org.saarus.service.task.Task;
import org.saarus.service.task.TaskManager;
import org.saarus.service.task.TaskResult;
import org.saarus.service.util.JSONSerializer;

public class TaskManagerUnitTest {
  static String TASK_JSON = 
  "{" +
  "  \"id\" : \"Dummy\"," +
  "  \"description\" : \"execute some hive sql queries....\"," +
  "  \"taskHandler\" : \"Dummy\"," +
  "  \"taskUnits\" : [ {" +
  "    \"name\" : \"hello\"," +
  "    \"description\" : \"Test hello method, expect return 'hello from Dummy...' string\"," +
  "    \"task\" : [\"hello taskUnit(not used)\"]" +
  "  }, {" +
  "    \"name\" : \"ping\"," +
  "    \"description\" : \"Test ping method, expect return 'true/false'\"," +
  "    \"task\" : [\"ping(not used)\"]" +
  "  }, {" +
  "    \"name\" : \"wait\"," +
  "    \"description\" : \"Test wait method, expect return '${time}' handle on the server\"," +
  "    \"task\" : [\"wait(not used)\"]," +
  "    \"parameters\" : {" +
  "      \"wait\" : 1000" +
  "     }" +
  "  }]" +
  "}" ;
  
  static String LONG_TASK_JSON = 
      "{" +
      "  \"id\" : \"long-taskUnit\"," +
      "  \"owner\" : \"tester\"," +
      "  \"description\" : \"execute some hive sql queries....\"," +
      "  \"taskHandler\" : \"Dummy\"," +
      "  \"taskSubmitWait\" : 2000," +
      "  \"taskUnits\" : [ {" +
      "    \"name\" : \"wait\"," +
      "    \"description\" : \"Test wait method, expect return '${time}' handle on the server\"," +
      "    \"task\" : [\"wait(not used)\"]," +
      "    \"parameters\" : {" +
      "      \"wait\" : 5000" +
      "     }" +
      "  }]" +
      "}" ;
  
  @Test
  public void testTaskManager() throws Exception {
    TaskManager manager = new TaskManager() ;
    manager.add(new DummyTaskUnitHandler()) ;
    Task task = JSONSerializer.JSON_SERIALIZER.fromString(TASK_JSON, Task.class) ;
    TaskResult result = manager.execute(task) ;
    System.out.println(JSONSerializer.JSON_SERIALIZER.toString(result));
    
    System.out.println("------------------------------------------------------------------");
    
    Task longTask = JSONSerializer.JSON_SERIALIZER.fromString(LONG_TASK_JSON, Task.class) ;
    TaskResult longTaskResult = manager.execute(longTask) ;
    Assert.assertEquals(0, longTaskResult.getTaskUnitResults().size()) ;
    Assert.assertTrue(!longTaskResult.isFinished()) ;
    while(!longTaskResult.isFinished()) {
      Thread.sleep(1000) ;
      System.out.println("poll to check the result......");
      longTaskResult = manager.poll(longTask.getOwner(), longTask.getId()) ;
    }
    System.out.println(JSONSerializer.JSON_SERIALIZER.toString(longTaskResult));
  }
}