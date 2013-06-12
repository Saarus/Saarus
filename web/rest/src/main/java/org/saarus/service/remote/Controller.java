package org.saarus.service.remote;

import java.util.ArrayList;
import java.util.List;

import org.saarus.service.remote.mahout.MahoutController;
import org.saarus.service.task.Task;
import org.saarus.service.task.TaskManager;
import org.saarus.service.task.TaskResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@org.springframework.stereotype.Controller
@RequestMapping("/")
public class Controller {
  @Autowired
  private TaskManager taskManager ;
  
  private List<ServiceDescription> services = new ArrayList<ServiceDescription>() ;
  
  public Controller() {
    services.add(MahoutController.HELP) ;
  }
  
  @RequestMapping(value="/help", method=RequestMethod.GET)
  public @ResponseBody List<ServiceDescription> help() {
    return services ;
  }
  
  @RequestMapping(value="/add", method=RequestMethod.POST)
  public  @ResponseBody ServiceDescription add(@RequestBody ServiceDescription desc) {
    System.out.println("Call add " + desc.getName()) ;
    desc.setName(desc.getName() + "(Added)") ;
    return desc;
  }
  
  @RequestMapping(value="/execute", method=RequestMethod.POST)
  public  @ResponseBody TaskResult execute(@RequestBody Task task) {
    return taskManager.execute(task) ;
  }
  
  @RequestMapping(value="/taskUnit/{owner}/{id}", method=RequestMethod.GET)
  public  @ResponseBody TaskResult task(@PathVariable String owner, @PathVariable String id) {
    return taskManager.poll(owner, id);
  }
}