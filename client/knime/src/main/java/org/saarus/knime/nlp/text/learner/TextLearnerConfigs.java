package org.saarus.knime.nlp.text.learner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.saarus.service.nlp.NLPLiblinearTrainTextConfig;
import org.saarus.service.nlp.NLPTaskHandler;
import org.saarus.service.task.Task;
import org.saarus.service.task.TaskUnit;
import org.saarus.util.json.JSONSerializer;

public class TextLearnerConfigs {
  final static public String DESCRIPTION = "description" ;
  final static public String TEXT_LEARNER_CONFIG = "text.learner.config" ;
  
  String description = "";
  NLPLiblinearTrainTextConfig config = new NLPLiblinearTrainTextConfig();
  
  public TextLearnerConfigs() {}
  
  public TextLearnerConfigs(NodeSettingsRO settings) throws InvalidSettingsException {
    if(!settings.containsKey(TEXT_LEARNER_CONFIG)) return ;
    try {
      description = settings.getString(DESCRIPTION) ;
      String configJson = settings.getString(TEXT_LEARNER_CONFIG) ;
      config = JSONSerializer.JSON_SERIALIZER.fromString(configJson, NLPLiblinearTrainTextConfig.class);
    } catch (IOException e) {
      throw new InvalidSettingsException(e) ;
    }
  }
  
  public void saveSettings(NodeSettingsWO settings) {
    try {
      settings.addString(DESCRIPTION, description) ;
      String configJson = JSONSerializer.JSON_SERIALIZER.toString(config);
      settings.addString(TEXT_LEARNER_CONFIG, configJson) ;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void merge(TextLearnerConfigs other) {
    config = other.config ;
  }
  
  public Task getGeneratedTask() throws IOException {
    Task task = new Task() ;
    task.setTaskHandler(NLPTaskHandler.NAME) ;
    task.setTaskSubmitWait(3000l) ;
    task.setDescription("Run NLP Text Training") ;
    List<TaskUnit> units = new ArrayList<TaskUnit>() ;
    TaskUnit trainTaskUnit = new TaskUnit() ;
    trainTaskUnit.setName("liblinearTrainText") ;
    trainTaskUnit.setTaskUnitConfig(config) ;
    units.add(trainTaskUnit) ;
    task.setTaskUnits(units) ;
    return task ;
  }
}
