package org.saarus.client;

import org.saarus.service.task.Task;
import org.saarus.service.task.TaskResult;
import org.saarus.service.util.CommandParser;
import org.saarus.service.util.JSONReader;
import org.saarus.util.json.JSONSerializer;
import org.springframework.web.client.RestTemplate;

public class RESTClient {
  
  private RestTemplate restTemplate;
  private String restUrl ;
  private HiveClient hiveClient ;
  
  public RESTClient() {
  }

  public RestTemplate getRestTemplate() { return this.restTemplate ; }
  public void setRestTemplate(RestTemplate tmpl) { this.restTemplate = tmpl ; }
  
  public String getRestUrl() { return this.restUrl ; }
  public void   setRestUrl(String url) { this.restUrl = url ; }
  
  public HiveClient getHiveClient() { 
    if(hiveClient == null) hiveClient = new HiveClient(restTemplate, restUrl) ; 
    return hiveClient ;
  }
  
  public TaskResult submitTask(Task task) throws Exception {
    TaskResult taskResult = 
        restTemplate.postForObject(restUrl + "/execute", task, TaskResult.class);
    return taskResult ;
  }
  
  public TaskResult pollTask(Task task) throws Exception {
    TaskResult taskResult = 
        restTemplate.getForObject(restUrl + "/taskUnit/" + task.getOwner() +"/" + task.getId(), TaskResult.class);
    return taskResult ;
  }

  public void runTask(Task task) throws Exception {
    System.out.println();
    System.out.println("************************************************************************");
    System.out.println("Run taskUnit: " + task.getDescription());
    System.out.println("************************************************************************");
    TaskResult taskResult = submitTask(task);
    while(!taskResult.isFinished()) {
      Thread.sleep(1000) ;
      System.out.print(".");
      taskResult = pollTask(taskResult.getTask()) ;
    }
    System.out.println() ;
    System.out.println(JSONSerializer.JSON_SERIALIZER.toString(taskResult));
  }
  
  public void runFile(String jsonFile) throws Exception {
    JSONReader jsonReader = new JSONReader(jsonFile, false) ;
    Task task = null; 
    while((task = jsonReader.read(Task.class)) != null) {
      runTask(task) ;
    }
  }
  
  static public void main(String[] args) throws Exception {
    if(args == null || args.length == 0) {
      args = new String[] {"-file", "src/app/script/test-hive-script.json"} ;
    }
    
    CommandParser command = new CommandParser("Run:") ;
    command.addMandatoryOption("file", true, "The json script file") ;
    if(!command.parse(args)) return ;
    command.printHelp() ;
    String file = command.getOption("file", null) ;
    
    ClientContext clientContext = new ClientContext() ;
    RESTClient runner = clientContext.getBean(RESTClient.class) ;
    runner.runFile(file) ;
  }
}
