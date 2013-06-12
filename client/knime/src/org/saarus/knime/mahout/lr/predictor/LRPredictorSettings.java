package org.saarus.knime.mahout.lr.predictor;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.config.ConfigRO;
import org.knime.core.node.config.ConfigWO;

public class LRPredictorSettings {
  final static String PREFIX = "lrpredictor:" ;
  final static String QUERY_NAMES = PREFIX + "names" ;
  
  private Map<String, FileImportSetting> fileSettings = new LinkedHashMap<String, FileImportSetting>() ;
 
  public LRPredictorSettings() {}
  
  public LRPredictorSettings(NodeSettingsRO settings) throws InvalidSettingsException {
    if(!settings.containsKey(QUERY_NAMES)) return ;
    String names = settings.getString(QUERY_NAMES) ;
    String[] name = names.split(",") ;
    for(String selName : name) {
      ConfigRO config = settings.getConfig(PREFIX + selName) ;
      FileImportSetting fsetting = 
          new FileImportSetting(config.getString("table"), 
                                config.getString("description"), 
                                config.getString("query")) ;
      fileSettings.put(selName, fsetting) ;
    }
  }
  
  public void saveSettings(NodeSettingsWO settings) {
    if(fileSettings.size() == 0) return ;
    
    StringBuilder names = new StringBuilder() ;
    Iterator<FileImportSetting> i = fileSettings.values().iterator() ;
    while(i.hasNext()) {
      FileImportSetting sel = i.next() ;
      if(names.length() > 0) names.append(",") ;
      names.append(sel.name) ;
      ConfigWO config = settings.addConfig(PREFIX  + sel.name) ;
      config.addString("table", sel.name) ;
      config.addString("description", sel.description) ;
      config.addString("query", sel.query) ;
    }
    settings.addString(QUERY_NAMES, names.toString()) ;
  }
  
  public void merge(LRPredictorSettings other) {
    System.out.println("Current setting has " + fileSettings.size() + " config");
    System.out.println("Other setting has " + fileSettings.size() + " config");
    
    Iterator<FileImportSetting> i = other.fileSettings.values().iterator() ;
    while(i.hasNext()) {
      FileImportSetting sel = i.next() ;
      fileSettings.put(sel.name, sel) ;
    }
  }
  
  public void add(String name, String desc, String query) {
    fileSettings.put(name, new FileImportSetting(name, desc, query)) ;
  }
  
  public String toString() {
    StringBuilder b = new StringBuilder() ;
    b.append("Config:\n") ;
    Iterator<FileImportSetting> i = fileSettings.values().iterator() ;
    while(i.hasNext()) {
      FileImportSetting sel = i.next() ;
      b.append("Name = ").append(sel.name).
        append(", Desc = ").append(sel.description).
        append(", Query = ").append(sel.query).append("\n") ;
    }
    return b.toString() ;
  }
  
  static public class FileImportSetting {
    String name ;
    String description ;
    String query ;
    
    FileImportSetting(String name, String desc, String query) {
      this.name = name ;
      this.description = desc ;
      this.query = query ;
    }
  }
}
