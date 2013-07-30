package org.saarus.nlp.classify.liblinear;

import java.util.List;

public interface FeatureGenerator<T1, T2> {
  public List<String> extractFeatures(T1 candidate, T2 context);
}
