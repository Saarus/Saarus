package org.saarus.service.remote.hive;

import java.util.List;

import org.saarus.service.remote.ServiceDescription;
import org.saarus.service.sql.QueryResult;
import org.saarus.service.sql.TableMetadata;
import org.saarus.service.sql.hive.HiveTaskHandler;
import org.saarus.service.task.TaskUnit;
import org.saarus.service.task.TaskUnitResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@org.springframework.stereotype.Controller
@RequestMapping("/hive")
public class HiveController {
  final static public ServiceDescription HELP = new ServiceDescription("hive", "Hive service") ;
  
  @Autowired
  private HiveTaskHandler hiveClient ;
  
  @RequestMapping(value="/help", method=RequestMethod.GET)
  public @ResponseBody ServiceDescription help() {
    return HELP ;
  }
  
  @RequestMapping(value="/table/list", method=RequestMethod.GET)
  public @ResponseBody TaskUnitResult<List<String>> tableList(@RequestParam(value = "forceUpdate", defaultValue = "false") boolean forceUpdate) throws Exception {
    TaskUnitResult<List<String>> result = hiveClient.listTables() ;
    System.out.println(result.getResult());
    return result ;
  }
  
  @RequestMapping(value="/table/desc/{name}", method=RequestMethod.GET)
  public @ResponseBody TaskUnitResult<TableMetadata> tableDesc(@PathVariable String name) throws Exception {
    TaskUnitResult<TableMetadata>  result =  hiveClient.describeTable(name) ;
    return result ;
  }
  
  
  @RequestMapping(value="/query", method=RequestMethod.GET)
  public @ResponseBody TaskUnitResult<QueryResult> query(@RequestParam String q) throws Exception {
    return hiveClient.executeQuery(q) ;
  }

  @RequestMapping(value="/execute", method=RequestMethod.GET)
  public @ResponseBody TaskUnitResult<Boolean> execute(@RequestParam String q) throws Exception {
    return hiveClient.execute(q) ;
  }
  
  public @ResponseBody TaskUnitResult<Boolean> executeTask(@RequestParam TaskUnit unit) throws Exception {
    return null ;
  }
}