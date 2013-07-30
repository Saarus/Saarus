
package org.saarus.nlp.classify.liblinear;

import java.util.ArrayList;
import java.util.List;

import org.saarus.nlp.token.IToken;

public class FeatureGenerators {
  private FeatureGenerator<IToken[], String>[] mFeatureGenerators;

  public FeatureGenerators(FeatureGenerator<IToken[], String> ... mFeatureGenerators) {		
    this.mFeatureGenerators = mFeatureGenerators;
  }

  public ArrayList<String> getContext(IToken[] tokens, String sentence) {
    ArrayList<String> context = new ArrayList<String>();
    for (FeatureGenerator<IToken[], String> generator : mFeatureGenerators) {
      List<String> features = generator.extractFeatures(tokens, sentence);
      context.addAll(features);
    }
    return context;
  }
}