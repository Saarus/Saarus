package org.saarus.service.nlp.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
import org.saarus.nlp.classify.liblinear.TextClassifier;
import org.saarus.service.hadoop.util.FSResource;
/**
 * UDFLiblinearTextClassify.
 */
@Description(name = "text_classify",
  value = "_FUNC_(textColumn, 'modelConfig')" + 
          "textColumn - is the data column that contain the text." +
          "modelConfig - is the TABLE_LOCATION that contains the the predicting model and other configuration." +
          "EX: SELECT _FUNC_(text, '/tmp/text-model-conf') FROM twitter;\n"
)
public class UDFLiblinearTextClassify extends UDF {
  private final Text currentConfDir = new Text();

  private TextClassifier classifier ;
  private Text result = new Text();
  private Throwable error ;
  
  public Text evaluate(Text text, Text confDir) {
    if (text == null || confDir == null) {
      return null;
    }
    if(error != null) {
      result.set(error.getMessage()) ;
      return result ;
    }
    
    try {
      if(!confDir.equals(currentConfDir)) {
        currentConfDir.set(confDir) ;
        classifier = createTextClassifier(confDir.toString()) ;
      }

      double predict = classifier.classify(text.toString());
      String label = classifier.getFeatureSet().getLabels().get((int) predict);
      result.set(label);
    } catch (Throwable t) {
      error = t ;
      result.set(t.getMessage()) ;
    }
    return result;
  }

  private TextClassifier createTextClassifier(String modelDir) throws Exception {
    FSResource modelRes  = FSResource.get(modelDir + "/text-classify.model");
    FSResource dictRes  = FSResource.get(modelDir + "/text-classify.dict");
    TextClassifier classifier;
    classifier = new TextClassifier(modelRes.getInputStream(), dictRes.getInputStream());
    return classifier ;
  }
}
