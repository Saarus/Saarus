package org.saarus.knime.data.stat;

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

public class StatisticConfigs {
  final static String PREFIX = "query:" ;
  final static String QUERY_NAMES = PREFIX + "names" ;
  
  private Map<String, StatisticConfig> configs = new LinkedHashMap<String, StatisticConfig>() ;
 
  public StatisticConfigs() {}
  
  public StatisticConfigs(NodeSettingsRO settings) throws InvalidSettingsException {
    if(!settings.containsKey(QUERY_NAMES)) return ;
    String names = settings.getString(QUERY_NAMES) ;
    String[] name = names.split(",") ;
    for(String selName : name) {
      ConfigRO config = settings.getConfig(PREFIX + selName) ;
      StatisticConfig sconfig = 
          new StatisticConfig(config.getString("table"), config.getString("description"),  config.getString("query")) ;
      configs.put(selName, sconfig) ;
    }
  }
  
  public Collection<StatisticConfig> getConfigs() { return this.configs.values() ; }
  
  public void saveSettings(NodeSettingsWO settings) {
    if(configs.size() == 0) return ;
    
    StringBuilder names = new StringBuilder() ;
    Iterator<StatisticConfig> i = configs.values().iterator() ;
    while(i.hasNext()) {
      StatisticConfig sel = i.next() ;
      if(names.length() > 0) names.append(",") ;
      names.append(sel.table) ;
      ConfigWO config = settings.addConfig(PREFIX  + sel.table) ;
      config.addString("table", sel.table) ;
      config.addString("description", sel.description) ;
      config.addString("query", sel.query) ;
    }
    settings.addString(QUERY_NAMES, names.toString()) ;
  }
  
  public void merge(StatisticConfigs other) {
    System.out.println("Current setting has " + configs.size() + " config");
    System.out.println("Other setting has " + configs.size() + " config");
    
    Iterator<StatisticConfig> i = other.configs.values().iterator() ;
    while(i.hasNext()) {
      StatisticConfig sel = i.next() ;
      configs.put(sel.table, sel) ;
    }
  }
  
  public void add(String name, String desc, String query) {
    configs.put(name, new StatisticConfig(name, desc, query)) ;
  }
  
  public void add(StatisticConfig config) {
    configs.put(config.table, config) ;
  }
  
  public Task getGeneratedTask() throws IOException {
    Task task = new Task() ;
    List<TaskUnit> units = new ArrayList<TaskUnit>() ;
    
    TaskUnit addJar = new TaskUnit() ;
    addJar.setName("execute") ;
    addJar.setTaskLine("ADD jar /home/hadoop/yelp/lib/saarus.service.hive-1.0.jar");
    units.add(addJar) ;
    
    TaskUnit registerFieldStat = new TaskUnit() ;
    registerFieldStat.setName("execute") ;
    registerFieldStat.setTaskLine("CREATE TEMPORARY FUNCTION field_stat AS 'org.saarus.service.hive.func.UDAFFieldStat'");
    units.add(registerFieldStat) ;
    
    TaskUnit registerStringFieldStat = new TaskUnit() ;
    registerStringFieldStat.setName("execute") ;
    registerStringFieldStat.setTaskLine("CREATE TEMPORARY FUNCTION string_field_stat AS 'org.saarus.service.hive.func.UDAFStringFieldStat'");
    units.add(registerStringFieldStat) ;
  
    TaskUnit registerNumberFieldStat = new TaskUnit() ;
    registerNumberFieldStat.setName("execute") ;
    registerNumberFieldStat.setTaskLine("CREATE TEMPORARY FUNCTION number_field_stat AS 'org.saarus.service.hive.func.UDAFNumberFieldStat'");
    units.add(registerNumberFieldStat) ;
    
    Iterator<StatisticConfig> i = configs.values().iterator() ;
    while(i.hasNext()) {
      StatisticConfig config = i.next() ;
      for(TaskUnit sel : config.getGeneratedTaskUnits()) {
        units.add(sel) ;
      }
    }
    task.setTaskHandler("HiveService") ;
    task.setTaskUnits(units) ;
    task.setTaskSubmitWait(3000l) ;
    task.setDescription("Run table field statistic") ;
    return task ;
  }
  
  public String toString() {
    StringBuilder b = new StringBuilder() ;
    b.append("Config:\n") ;
    Iterator<StatisticConfig> i = configs.values().iterator() ;
    while(i.hasNext()) {
      StatisticConfig sel = i.next() ;
      b.append("Table = ").append(sel.table).
        append(", Desc = ").append(sel.description).
        append(", Query = ").append(sel.query).append("\n") ;
    }
    return b.toString() ;
  }
  
  static public class StatisticConfig {
    final static public String DEFAULT_NAME = "Table Name" ;
    final public String table ;
    final public String description ;
    final public String query ;
    
    StatisticConfig() {
      this.table = DEFAULT_NAME ;
      this.description = "Your description!!!" ;
      this.query = "" ;
    }
    
    StatisticConfig(String table, String desc, String query) {
      this.table = table ;
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
        taskUnit.setId(getTaskUnitId()) ;
        taskUnit.setName("executeQuery") ;
        taskUnit.setTaskLine(query[i].trim()) ;
        holder.add(taskUnit) ;
      }
      return holder.toArray(new TaskUnit[holder.size()])  ;
    }
    
    public String getTaskUnitId() {
      return "field-statistic-" + table;
    }
  }
}
