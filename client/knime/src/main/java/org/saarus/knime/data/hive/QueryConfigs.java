package org.saarus.knime.data.hive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.config.ConfigRO;
import org.knime.core.node.config.ConfigWO;
import org.saarus.service.task.Task;
import org.saarus.service.task.TaskUnit;

public class QueryConfigs {
  final static String PREFIX = "query:" ;
  final static String QUERY_NAMES = PREFIX + "names" ;
  
  private Map<String, QueryConfig> configs = new LinkedHashMap<String, QueryConfig>() ;
 
  public QueryConfigs() {}
  
  public QueryConfigs(NodeSettingsRO settings) throws InvalidSettingsException {
    if(!settings.containsKey(QUERY_NAMES)) return ;
    String names = settings.getString(QUERY_NAMES) ;
    String[] name = names.split(",") ;
    for(String selName : name) {
      ConfigRO config = settings.getConfig(PREFIX + selName) ;
      QueryConfig fsetting = 
          new QueryConfig(config.getString("table"), config.getString("description"),  config.getString("query")) ;
      configs.put(selName, fsetting) ;
    }
  }
  
  public Collection<QueryConfig> getConfigs() { return this.configs.values() ; }
  
  public void saveSettings(NodeSettingsWO settings) {
    if(configs.size() == 0) return ;
    
    StringBuilder names = new StringBuilder() ;
    Iterator<QueryConfig> i = configs.values().iterator() ;
    while(i.hasNext()) {
      QueryConfig sel = i.next() ;
      if(names.length() > 0) names.append(",") ;
      names.append(sel.name) ;
      ConfigWO config = settings.addConfig(PREFIX  + sel.name) ;
      config.addString("table", sel.name) ;
      config.addString("description", sel.description) ;
      config.addString("query", sel.query) ;
    }
    settings.addString(QUERY_NAMES, names.toString()) ;
  }
  
  public void merge(QueryConfigs other) {
    System.out.println("Current setting has " + configs.size() + " config");
    System.out.println("Other setting has " + configs.size() + " config");
    
    Iterator<QueryConfig> i = other.configs.values().iterator() ;
    while(i.hasNext()) {
      QueryConfig sel = i.next() ;
      configs.put(sel.name, sel) ;
    }
  }
  
  public void add(String name, String desc, String query) {
    configs.put(name, new QueryConfig(name, desc, query)) ;
  }
  
  public void add(QueryConfig config) {
    configs.put(config.name, config) ;
  }
  
  public Task getGeneratedTask() throws IOException {
    Task task = new Task() ;
    List<TaskUnit> units = new ArrayList<TaskUnit>() ;
    Iterator<QueryConfig> i = configs.values().iterator() ;
    while(i.hasNext()) {
      QueryConfig config = i.next() ;
      for(TaskUnit sel : config.getGeneratedTaskUnits()) {
        units.add(sel) ;
      }
    }
    task.setTaskHandler("SQLService") ;
    task.setTaskUnits(units) ;
    task.setTaskSubmitWait(3000l) ;
    task.setDescription("create json tables") ;
    return task ;
  }
  
  public String toString() {
    StringBuilder b = new StringBuilder() ;
    b.append("Config:\n") ;
    Iterator<QueryConfig> i = configs.values().iterator() ;
    while(i.hasNext()) {
      QueryConfig sel = i.next() ;
      b.append("Name = ").append(sel.name).
        append(", Desc = ").append(sel.description).
        append(", Query = ").append(sel.query).append("\n") ;
    }
    return b.toString() ;
  }
  
  static public class QueryConfig {
    final public String name ;
    final public String description ;
    final public String query ;
    
    QueryConfig() {
      this.name = "table" ;
      this.description = "description" ;
      this.query = "" ;
    }
    
    QueryConfig(String name, String desc, String query) {
      this.name = name ;
      this.description = desc ;
      this.query = query ;
    }
    
    public TaskUnit[] getGeneratedTaskUnits() {
      String[] query = this.query.split(";") ;
      List<TaskUnit> holder = new ArrayList<TaskUnit>() ;
      for(int i = 0; i < query.length; i++) {
        String selQuery = query[i].trim() ;
        if(selQuery.length() == 0) continue ;
        TaskUnit taskUnit = new TaskUnit() ;
        taskUnit.setName("execute") ;
        taskUnit.setTaskLine(query[i].trim()) ;
        holder.add(taskUnit) ;
      }
      return holder.toArray(new TaskUnit[holder.size()])  ;
    }
  }
}