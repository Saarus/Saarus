package sample.nlp.sentiment;

import java.util.ArrayList;
import java.util.List;

import sample.nlp.twitter.parsing.TweetToken;

public class SentimentBOWFeatureGenerator implements FeatureGenerator<List<TweetToken>, String> {
  public SentimentBOWFeatureGenerator() {
  }

  public ArrayList<String> extractFeatures(List<TweetToken> tokens, String sentence) {
    ArrayList<String> featureCollector = new ArrayList<String>();
    for (TweetToken token : tokens) {
      if (token.getTokenNormalized().size() != 0) {
        String lemma = token.getTokenNormalized().get("Lemma");
        featureCollector.add(lemma);
        //if (token.getTokenAnnotations().size() !=0)
        //	featureCollector.add("WP:" + lemma + "_" + token.getTokenAnnotations().get("POS"));
      }
    }

    return featureCollector;
  }
}
