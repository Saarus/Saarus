package org.saarus.nlp.classify.liblinear;

import java.util.ArrayList;

import org.saarus.nlp.en.EnglishMorphologyProcessor;
import org.saarus.nlp.token.IToken;
/**
 * Bag Of Word feature generator
 * @author headvances
 */
public class BagOfWordFeatureGenerator implements FeatureGenerator<IToken[], String> {
  private EnglishMorphologyProcessor morphologyProcessor ; ;
  
  public BagOfWordFeatureGenerator() {
    morphologyProcessor = new EnglishMorphologyProcessor() ;
  }
  
  public ArrayList<String> extractFeatures(IToken[] tokens, String sentence) {
    ArrayList<String> featureCollector = new ArrayList<String>();
    for (IToken token : tokens) {
      String normForm = token.getNormalizeForm();
      //add lema normalized and stem for english language
      normForm = morphologyProcessor.process(token.getNormalizeForm());
      featureCollector.add(normForm);
    }

    return featureCollector;
  }
}
